package org.jedi_bachelor.ioboxstarter.brokers;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class ActiveMQBrokerStrategy extends AbstractBroker implements BrokerStrategy {
    private JmsTemplate jmsTemplate;

    @Builder
    public ActiveMQBrokerStrategy(JmsTemplate jmsTemplate, DlqProperties dlqProperties) {
        super(dlqProperties);
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void publish(OutboxMessage message) {
        try {
            this.jmsTemplate.send(message.getTopic(), session -> {
                Message jmsMessage = session.createTextMessage(message.getPayload());

                jmsMessage.setStringProperty("messageId", message.getMessageId());
                jmsMessage.setStringProperty("queueName", message.getTopic());
                jmsMessage.setStringProperty("groupId", "default");
                jmsMessage.setLongProperty("timestamp", System.currentTimeMillis());

                return jmsMessage;
            });

            log.debug("Message {} published to queue {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to queue {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    @Override
    public void consume(String queueName, Consumer<MessageEnvelope> handler) {
        try {
            ConnectionFactory connectionFactory = this.jmsTemplate.getConnectionFactory();

            DefaultMessageListenerContainer container =
                    new org.springframework.jms.listener.DefaultMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setDestinationName(queueName);
            container.setMessageListener((MessageListener) message -> {
                try {
                    String payload = this.jmsTemplate.getMessageConverter()
                            .fromMessage(message)
                            .toString();

                    String messageId = message.getStringProperty("messageId");
                    String groupId = message.getStringProperty("groupId");
                    Long timestamp = message.getLongProperty("timestamp");

                    MessageEnvelope envelope = MessageEnvelope.builder()
                            .payload(payload)
                            .messageId(messageId)
                            .queueName(queueName)
                            .groupId(groupId != null ? groupId : "default")
                            .timestamp(timestamp)
                            .fallbackQueueName(queueName)
                            .build();

                    handler.accept(envelope);

                } catch (Exception e) {
                    log.error("Error processing message from queue {}", queueName, e);
                }
            });

            container.afterPropertiesSet();
            container.start();

            log.info("Started ActiveMQ consumer for queue: {}", queueName);

        } catch (Exception e) {
            log.error("Failed to start ActiveMQ consumer for queue: {}", queueName, e);
        }
    }

    @Override
    public void publishDeadLetter(DeadLettersEntity message) {
        if (!this.dlqProperties.isEnabled()) {
            return;
        }

        try {
            String queueName = this.dlqProperties.getDlqName();

            this.jmsTemplate.send(queueName, session -> {
                Message jmsMessage = session.createTextMessage(message.getPayload());

                jmsMessage.setStringProperty("messageId", message.getMessageId());
                jmsMessage.setStringProperty("errorMessage", message.getErrorMessage());

                return jmsMessage;
            });

            log.info("Dead letter {} published to DLQ", message.getMessageId());
        } catch (Exception e) {
            log.error("Failed to publish dead letter {}", message.getMessageId(), e);
            throw new RuntimeException("Failed to publish dead letter", e);
        }
    }
}
