package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
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
import java.util.function.Consumer;

@Component
@Slf4j
@Builder
public class KafkaBrokerStrategy implements BrokerStrategy {
    private KafkaTemplate<String, Object> kafkaTemplate;

    private DlqProperties dlqProperties;

    private Environment environment = new StandardEnvironment();

    @Override
    public void publish(OutboxMessage message) {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(
                    message.getTopic(),
                    message.getMessageId(),
                    message.getPayload()
            );

            record.headers()
                    .add("messageId", message.getMessageId().getBytes(StandardCharsets.UTF_8))
                    .add("queueName", message.getTopic().getBytes(StandardCharsets.UTF_8))
                    .add("groupId", "default".getBytes(StandardCharsets.UTF_8))
                    .add("timestamp", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));

            this.kafkaTemplate.send(record);

            log.debug("Message {} published to topic {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to topic {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    @Override
    public void consume(String queueName, Consumer<MessageEnvelope> handler) {
        try {
            ConcurrentMessageListenerContainer<String, String> container = createContainer(queueName);

            container.setupMessageListener((MessageListener<String, String>) record -> {
                try {
                    String payload = record.value();
                    String messageId = this.extractHeader(record, "messageId");
                    String groupId = this.extractHeader(record, "groupId");
                    String timestampHeader = this.extractHeader(record, "timestamp");

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
    public void publishDeadLetter(DeadLettersEntity message) {
        if (!this.dlqProperties.isEnabled()) {
            return;
        }

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

    private ConcurrentMessageListenerContainer<String, String> createContainer(String topic) {
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setAckMode(ContainerProperties.AckMode.MANUAL);

        DefaultKafkaConsumerFactory<String, String> consumerFactory =
                new DefaultKafkaConsumerFactory<>(this.consumerConfigs());

        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        String bootstrapServers = this.environment != null
                ? this.environment.getProperty("spring.kafka.bootstrap-servers", "localhost:9093")
                : "localhost:9093";

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        String groupId = this.environment != null
                ? this.environment.getProperty("spring.kafka.consumer.group-id", "inbox-group")
                : "inbox-group";
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    private String extractHeader(ConsumerRecord<String, String> record, String headerName) {
        if (record.headers() != null) {
            org.apache.kafka.common.header.Header header =
                    record.headers().lastHeader(headerName);
            if (header != null && header.value() != null) {
                return new String(header.value(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
