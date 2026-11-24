package org.jedi_bachelor.ioboxstarter.brokers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jedi_bachelor.ioboxstarter.constants.MessageHeaderConstants;
import org.jedi_bachelor.ioboxstarter.constants.PropertiesPathsConstants;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnClass(KafkaTemplate.class)
public final class KafkaBrokerStrategy extends AbstractBroker implements BrokerStrategy {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  private final Environment environment = new StandardEnvironment();

  public KafkaBrokerStrategy(
      KafkaTemplate<String, Object> kafkaTemplate, String deadLettersQueueName) {
    super(deadLettersQueueName);
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(final OutboxMessage message) {
    Objects.requireNonNull(message, "Outbox message can't be null");

    try {
      ProducerRecord<String, Object> record =
          new ProducerRecord<>(message.getQueueName(), message.getMessageId(), message.getPayload());

      record
          .headers()
          .add(MessageHeaderConstants.MESSAGE_ID, message.getMessageId().getBytes(StandardCharsets.UTF_8))
          .add(MessageHeaderConstants.QUEUE_NAME, message.getQueueName().getBytes(StandardCharsets.UTF_8))
          .add(
              MessageHeaderConstants.GROUP_ID,
              environment
                  .getProperty(PropertiesPathsConstants.KAFKA_GROUP_ID_PATH, MessageHeaderConstants.Default.DEFAULT_GROUP_ID)
                  .getBytes(StandardCharsets.UTF_8))
          .add(
              MessageHeaderConstants.TIMESTAMP,
              String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));

      kafkaTemplate.send(record);

      log.debug("Message {} published to topic {}", message.getMessageId(), message.getQueueName());
    } catch (Exception e) {
      log.error(
          "Failed to publish message {} to topic {}",
          message.getMessageId(),
          message.getQueueName(),
          e);
      throw new RuntimeException("Failed to publish message", e);
    }
  }

  @Override
  public void consume(final String queueName, final Consumer<MessageEnvelope> handler) {
    Objects.requireNonNull(queueName, "Queue name can't be null");
    Objects.requireNonNull(handler, "Message handler can't be null");

    try {
      ConcurrentMessageListenerContainer<String, String> container = createContainer(queueName);

      container.setupMessageListener(
          (MessageListener<String, String>)
              record -> {
                try {
                  String payload = record.value();
                  String messageId = extractHeader(record, MessageHeaderConstants.MESSAGE_ID);
                  String groupId = extractHeader(record, MessageHeaderConstants.GROUP_ID);
                  groupId = (groupId != null ? groupId : MessageHeaderConstants.Default.DEFAULT_GROUP_ID);
                  String timestampHeader = extractHeader(record, MessageHeaderConstants.TIMESTAMP);

                  MessageEnvelope envelope =
                      MessageEnvelope.builder()
                          .payload(payload)
                          .messageId(messageId)
                          .queueName(queueName)
                          .groupId(groupId)
                          .timestamp(Long.parseLong(timestampHeader))
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
    Objects.requireNonNull(message, "Dlq message can't be null");

    try {
      ProducerRecord<String, Object> record =
          new ProducerRecord<>(deadLettersQueueName, message.getMessageId(), message.getPayload());

      record
          .headers()
          .add(MessageHeaderConstants.MESSAGE_ID, message.getMessageId().getBytes(StandardCharsets.UTF_8))
          .add(MessageHeaderConstants.ERROR_MESSAGE, message.getErrorMessage().getBytes(StandardCharsets.UTF_8));

      kafkaTemplate.send(record);

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

    String bootstrapServers =
        environment.getProperty(PropertiesPathsConstants.KAFKA_BOOTSTRAP_SERVER_PATH, "localhost:9093");

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    String groupId = environment.getProperty(PropertiesPathsConstants.KAFKA_GROUP_ID_PATH, "inbox-group");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    return props;
  }

  private String extractHeader(
      final ConsumerRecord<String, String> record, final String headerName) {
    if (record.headers() != null) {
      Header header = record.headers().lastHeader(headerName);
      if (header != null && header.value() != null) {
        return new String(header.value(), StandardCharsets.UTF_8);
      }
    }
    return null;
  }
}
