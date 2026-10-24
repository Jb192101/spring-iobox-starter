package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public final class RedisBrokerStrategy extends AbstractBroker implements BrokerStrategy {

    private static final String GROUP_ID_FIELD = "groupId";

    private static final String DEFAULT_GROUP = "default";

    private static final String DLQ_STREAM_FIELD_MESSAGE_ID = "messageId";

    private static final String DLQ_STREAM_FIELD_ERROR = "errorMessage";

    private final RedisTemplate<String, String> redisTemplate;

    private volatile StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;

    @Builder
    public RedisBrokerStrategy(
            DlqProperties dlqProperties,
            RedisTemplate<String, String> redisTemplate
    ) {
        super(dlqProperties);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(OutboxMessage message) {
        try {
            StreamOperations<String, String, String> ops = redisTemplate.opsForStream();

            Map<String, String> fields = new HashMap<>();
            fields.put("messageId", message.getMessageId());
            fields.put("payload", message.getPayload());
            fields.put("queueName", message.getTopic());
            fields.put(GROUP_ID_FIELD, DEFAULT_GROUP);
            fields.put("timestamp", String.valueOf(System.currentTimeMillis()));

            ops.add(message.getTopic(), fields);

            log.debug("Message {} published to Redis stream {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to Redis stream {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    @Override
    public void consume(String queueName, Consumer<MessageEnvelope> handler) {
        try {
            RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();

            ensureConsumerGroup(queueName);

            StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                    StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                            .builder()
                            .pollTimeout(Duration.ofSeconds(2))
                            .build();

            this.container = StreamMessageListenerContainer.create(connectionFactory, options);

            this.container.receive(
                    StreamOffset.create(queueName, ReadOffset.lastConsumed()),
                    message -> {
                        try {
                            Map<String, String> body = message.getValue();

                            MessageEnvelope envelope = MessageEnvelope.builder()
                                    .payload(body.get("payload"))
                                    .messageId(body.get("messageId"))
                                    .queueName(queueName)
                                    .groupId(body.getOrDefault(GROUP_ID_FIELD, DEFAULT_GROUP))
                                    .timestamp(parseLong(body.get("timestamp")))
                                    .fallbackQueueName(queueName)
                                    .build();

                            handler.accept(envelope);

                            redisTemplate.opsForStream().acknowledge(queueName, DEFAULT_GROUP, message.getId());

                        } catch (Exception e) {
                            log.error("Error processing message from Redis stream {}", queueName, e);
                        }
                    });

            this.container.start();

            log.info("Started Redis consumer for stream: {}", queueName);
        } catch (Exception e) {
            log.error("Failed to start Redis consumer for stream: {}", queueName, e);
        }
    }

    @Override
    public void publishDeadLetter(DlqMessage message) {
        if (!this.dlqProperties.isEnabled()) {
            return;
        }

        try {
            String streamName = this.dlqProperties.getDlqName();

            Map<String, String> fields = new HashMap<>();
            fields.put(DLQ_STREAM_FIELD_MESSAGE_ID, message.getMessageId());
            fields.put("payload", message.getPayload());
            fields.put(DLQ_STREAM_FIELD_ERROR, message.getErrorMessage());
            fields.put("timestamp", String.valueOf(System.currentTimeMillis()));

            redisTemplate.opsForStream().add(streamName, fields);

            log.info("Dead letter {} published to Redis stream {}", message.getMessageId(), streamName);
        } catch (Exception e) {
            log.error("Failed to publish dead letter {}", message.getMessageId(), e);
            throw new RuntimeException("Failed to publish dead letter", e);
        }
    }

    private void ensureConsumerGroup(String streamName) {
        StreamOperations<String, String, String> ops = redisTemplate.opsForStream();

        try {
            ops.createGroup(streamName, ReadOffset.from("0"), DEFAULT_GROUP);
            log.debug("Created consumer group {} for stream {}", DEFAULT_GROUP, streamName);
        } catch (Exception e) {
            log.debug("Consumer group {} already exists for stream {} (or stream empty)", DEFAULT_GROUP, streamName);
        }
    }

    private Long parseLong(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
