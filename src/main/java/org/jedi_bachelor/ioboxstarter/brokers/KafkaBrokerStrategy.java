package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@Slf4j
@ConditionalOnClass(KafkaTemplate.class)
public final class KafkaBrokerStrategy extends AbstractBroker implements BrokerStrategy {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final Environment environment = new StandardEnvironment();

    @Builder
    public KafkaBrokerStrategy(
            KafkaTemplate<String, Object> kafkaTemplate,
            DlqProperties dlqProperties
    ) {
        super(dlqProperties);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(final OutboxMessage message) {
        Objects.requireNonNull(message, "Outbox message can't be null");

        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(
                    message.getTopic(),
                    message.getMessageId(),
                    message.getPayload()
            );

            record.headers()
                    .add("messageId", message.getMessageId().getBytes(StandardCharsets.UTF_8))
                    .add("queueName", message.getTopic().getBytes(StandardCharsets.UTF_8))
                    .add("groupId", environment.getProperty("kafka.consumer.group-id", "default").getBytes(StandardCharsets.UTF_8))
                    .add("timestamp", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));

            kafkaTemplate.send(record);

            log.debug("Message {} published to topic {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to topic {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    @Override
    public void consume(final String queueName, final Consumer<MessageEnvelope> handler) {
        Objects.requireNonNull(queueName, "Queue name can't be null");
        Objects.requireNonNull(handler, "Message handler can't be null");

        try {
            ConcurrentMessageListenerContainer<String, String> container = createContainer(queueName);

            container.setupMessageListener((MessageListener<String, String>) record -> {
                try {
                    String payload = record.value();
                    String messageId = extractHeader(record, "messageId");
                    String groupId = extractHeader(record, "groupId");
                    String timestampHeader = extractHeader(record, "timestamp");

                    MessageEnvelope envelope = MessageEnvelope.builder()
                            .payload(payload)
                            .messageId(messageId)
                            .queueName(queueName)
                            .groupId(groupId != null ? groupId : "default")
                            .timestamp(timestampHeader != null ? Long.parseLong(timestampHeader) : null)
                            .fallbackQueueName(queueName)
                            .build();

                    handler.accept(envelope);

                } catch (Exception e) {
                    log.error("Error processing message from topic {}", queueName, e);
                }
            });

            container.start();
            log.info("Started Kafka consumer for topic: {}", queueName);

        } catch (Exception e) {
            log.error("Failed to start Kafka consumer for topic: {}", queueName, e);
        }
    }

    @Override
    public void publishDeadLetter(final DlqMessage message) {
        if (!this.dlqProperties.getEnabled()) {
            return;
        }

        Objects.requireNonNull(message, "Dlq message can't be null");

        try {
            String queueName = this.dlqProperties.getDlqName();

            ProducerRecord<String, Object> record = new ProducerRecord<>(
                    queueName,
                    message.getMessageId(),
                    message.getPayload()
            );

            record.headers()
                    .add("messageId", message.getMessageId().getBytes(StandardCharsets.UTF_8))
                    .add("errorMessage", message.getErrorMessage().getBytes(StandardCharsets.UTF_8));

            this.kafkaTemplate.send(record);

            log.info("Dead letter {} published", message.getMessageId());
        } catch (Exception e) {
            log.error("Failed to publish message {}", message.getMessageId(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    private ConcurrentMessageListenerContainer<String, String> createContainer(final String topic) {
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setAckMode(ContainerProperties.AckMode.RECORD);

        DefaultKafkaConsumerFactory<String, String> consumerFactory =
                new DefaultKafkaConsumerFactory<>(this.consumerConfigs());

        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        String bootstrapServers = environment.getProperty("spring.kafka.bootstrap-servers", "localhost:9093");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        String groupId = environment.getProperty("spring.kafka.consumer.group-id", "inbox-group");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    private String extractHeader(final ConsumerRecord<String, String> record, final String headerName) {
        if (record.headers() != null) {
            Header header =
                    record.headers().lastHeader(headerName);
            if (header != null && header.value() != null) {
                return new String(header.value(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
