package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@ConditionalOnClass(RabbitTemplate.class)
public final class RabbitMQBrokerStrategy extends AbstractBroker implements BrokerStrategy {

    private final RabbitTemplate rabbitTemplate;

    @Builder
    public RabbitMQBrokerStrategy(
            RabbitTemplate rabbitTemplate,
            DlqProperties dlqProperties
    ) {
        super(dlqProperties);
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(final OutboxMessage message) {
        Objects.requireNonNull(message, "Outbox message can't be null");

        try {
            rabbitTemplate.convertAndSend(message.getTopic(), message.getPayload());

            log.debug("Message {} published to queue {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to queue {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    @Override
    public void consume(final String queueName, final Consumer<MessageEnvelope> handler) {
        Objects.requireNonNull(queueName, "Queue name can't be null");
        Objects.requireNonNull(handler, "Message handler can't be null");

        try {
            rabbitTemplate.execute(channel -> {
                channel.queueDeclare(queueName, true, false, false, null);
                return null;
            });

            SimpleMessageListenerContainer container =
                    new SimpleMessageListenerContainer(rabbitTemplate.getConnectionFactory());

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
                            .messageId(messageId)
                            .queueName(queueName)
                            .groupId(groupId != null ? groupId.toString() : "default")
                            .timestamp(timestamp)
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
    public void publishDeadLetter(final DlqMessage message) {
        if(!dlqProperties.getEnabled()) {
            return;
        }

        try {
            String queueName = dlqProperties.getDlqName();

            rabbitTemplate.convertAndSend(queueName, message.getPayload());

            log.info("Dead letter {} published", message);
        } catch (Exception e) {
            log.error("Failed to publish message {}", message.getMessageId(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }
}
