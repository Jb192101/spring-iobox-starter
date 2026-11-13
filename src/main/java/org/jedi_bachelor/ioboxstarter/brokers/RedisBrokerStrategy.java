package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
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
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@ConditionalOnClass(RedisTemplate.class)
public final class RedisBrokerStrategy extends AbstractBroker implements BrokerStrategy {

    private final RedisTemplate<String, String> redisTemplate;

    private final Environment environment = new StandardEnvironment();

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
    public void publish(final OutboxMessage message) {
        Objects.requireNonNull(message, "Outbox message can't be null");

        try {
            StreamOperations<String, String, String> ops = redisTemplate.opsForStream();

            Map<String, String> fields = new HashMap<>();
            fields.put("messageId", message.getMessageId());
            fields.put("payload", message.getPayload());
            fields.put("queueName", message.getTopic());
            fields.put("groupId", environment.getProperty("kafka.data.redis.streams.group-id", "default"));
            fields.put("timestamp", String.valueOf(System.currentTimeMillis()));

            ops.add(message.getTopic(), fields);

            log.debug("Message {} published to Redis stream {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to Redis stream {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    @Override
    public void consume(final String queueName, final Consumer<MessageEnvelope> handler) {
        Objects.requireNonNull(queueName, "Queue name can't be null");
        Objects.requireNonNull(handler, "Message handler can't be null");

        try {
            RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();

            ensureConsumerGroup(queueName);

            StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                    StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                            .builder()
                            .pollTimeout(Duration.ofSeconds(2))
                            .build();

            assert connectionFactory != null;
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
                                    .groupId(body.getOrDefault("groupId", "default"))
                                    .timestamp(parseLong(body.get("timestamp")))
                                    .fallbackQueueName(queueName)
                                    .build();

                            handler.accept(envelope);

                            redisTemplate.opsForStream().acknowledge(queueName, environment.getProperty("kafka.data.redis.streams.group-id", "default"), message.getId());

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
    public void publishDeadLetter(final DlqMessage message) {
        if (!this.dlqProperties.getEnabled()) {
            return;
        }

        Objects.requireNonNull(message, "Dlq message can't be null");

        try {
            String streamName = this.dlqProperties.getDlqName();

            Map<String, String> fields = new HashMap<>();
            fields.put("messageId", message.getMessageId());
            fields.put("payload", message.getPayload());
            fields.put("errorMessage", message.getErrorMessage());
            fields.put("timestamp", String.valueOf(System.currentTimeMillis()));
            fields.put("groupId", message.getConsumerGroup());

            redisTemplate.opsForStream().add(streamName, fields);

            log.info("Dead letter {} published to Redis stream {}", message.getMessageId(), streamName);
        } catch (Exception e) {
            log.error("Failed to publish dead letter {}", message.getMessageId(), e);
            throw new RuntimeException("Failed to publish dead letter", e);
        }
    }

    private void ensureConsumerGroup(final String streamName) {
        StreamOperations<String, String, String> ops = redisTemplate.opsForStream();

        try {
            ops.createGroup(streamName, ReadOffset.from("0"), "default");
            log.debug("Created consumer group {} for stream {}", "default", streamName);
        } catch (Exception e) {
            log.debug("Consumer group {} already exists for stream {} (or stream empty)", "default", streamName);
        }
    }

    private Long parseLong(final String value) {
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
