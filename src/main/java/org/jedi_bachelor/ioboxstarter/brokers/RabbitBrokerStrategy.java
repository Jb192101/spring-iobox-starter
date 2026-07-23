package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
@Builder
public class RabbitBrokerStrategy implements BrokerStrategy {
    private RabbitTemplate rabbitTemplate;

    private DlqProperties dlqProperties;

    @Override
    public void publish(OutboxMessage message) {
        try {
            this.rabbitTemplate.convertAndSend(message.getTopic(), message.getPayload());

            log.debug("Message {} published to queue {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to queue {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    @Override
    public void consume(String queueName, Consumer<MessageEnvelope> handler) {
        try {
            this.rabbitTemplate.execute(channel -> {
                channel.queueDeclare(queueName, true, false, false, null);
                return null;
            });

            SimpleMessageListenerContainer container =
                    new SimpleMessageListenerContainer(this.rabbitTemplate.getConnectionFactory());

            container.setQueueNames(queueName);
            container.setAcknowledgeMode(
                    org.springframework.amqp.core.AcknowledgeMode.AUTO
            );
            container.setConcurrentConsumers(1);

            container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
                try {
                    String payload = new String(message.getBody(), StandardCharsets.UTF_8);
                    MessageProperties props = message.getMessageProperties();

                    String messageId = props.getHeader("messageId");
                    String groupId = props.getHeader("groupId");
                    Long timestamp = props.getHeader("timestamp");

                    MessageEnvelope envelope = MessageEnvelope.builder()
                            .payload(payload)
                            .messageId(messageId != null ? messageId.toString() : null)
                            .queueName(queueName)
                            .groupId(groupId != null ? groupId.toString() : "default")
                            .timestamp(timestamp != null ? (Long) timestamp : null)
                            .fallbackQueueName(queueName)
                            .build();

                    handler.accept(envelope);

                } catch (Exception e) {
                    log.error("Error processing message from queue {}", queueName, e);
                }
            });

            container.start();
            log.info("Started RabbitMQ consumer for queue: {}", queueName);

        } catch (Exception e) {
            log.error("Failed to start RabbitMQ consumer for queue: {}", queueName, e);
        }
    }

    @Override
    public void publishDeadLetter(DeadLettersEntity message) {
        if(!this.dlqProperties.isEnabled()) {
            return;
        }

        try {
            String queueName = this.dlqProperties.getDlqName();

            this.rabbitTemplate.convertAndSend(queueName, message.getPayload());

            log.info("Dead letter {} published", message);
        } catch (Exception e) {
            log.error("Failed to publish message {}", message.getMessageId(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }
}
