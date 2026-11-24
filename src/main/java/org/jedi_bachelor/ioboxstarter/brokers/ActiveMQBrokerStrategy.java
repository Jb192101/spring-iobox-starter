package org.jedi_bachelor.ioboxstarter.brokers;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.constants.MessageHeaderConstants;
import org.jedi_bachelor.ioboxstarter.constants.PropertiesPathsConstants;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnClass(JmsTemplate.class)
public final class ActiveMQBrokerStrategy extends AbstractBroker implements BrokerStrategy {

  private final JmsTemplate jmsTemplate;

  private final Environment environment = new StandardEnvironment();

  @Builder
  public ActiveMQBrokerStrategy(JmsTemplate jmsTemplate, String dlqName) {
    super(dlqName);

    this.jmsTemplate = jmsTemplate;
  }

  @Override
  public void publish(final OutboxMessage message) {
    Objects.requireNonNull(message, "Outbox message can't be null");

    try {
        final String groupId = environment.getProperty(PropertiesPathsConstants.ACTIVEMQ_GROUP_ID_PATH, MessageHeaderConstants.Default.DEFAULT_GROUP_ID);
      jmsTemplate.send(
          message.getQueueName(),
          session -> {
            Message jmsMessage = session.createTextMessage(message.getPayload());

            jmsMessage.setStringProperty(MessageHeaderConstants.MESSAGE_ID, message.getMessageId());
            jmsMessage.setStringProperty("queueName", message.getQueueName());
            jmsMessage.setStringProperty("JMSXGroupID", groupId);
            jmsMessage.setLongProperty(MessageHeaderConstants.TIMESTAMP, System.currentTimeMillis());

            return jmsMessage;
          });

      log.debug("Message {} published to queue {}", message.getMessageId(), message.getQueueName());
    } catch (Exception e) {
      log.error(
          "Failed to publish message {} to queue {}",
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
      ConnectionFactory connectionFactory = jmsTemplate.getConnectionFactory();

      DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
      container.setConnectionFactory(connectionFactory);
      container.setDestinationName(queueName);
      container.setMessageListener(
          (MessageListener)
              message -> {
                try {
                  String payload =
                      Objects.requireNonNull(jmsTemplate.getMessageConverter())
                          .fromMessage(message)
                          .toString();

                  String messageId = message.getStringProperty(MessageHeaderConstants.MESSAGE_ID);
                  String groupId = message.getStringProperty(MessageHeaderConstants.JMS_GROUP_ID);
                  long timestamp = message.getLongProperty(MessageHeaderConstants.TIMESTAMP);

                  MessageEnvelope envelope =
                      MessageEnvelope.builder()
                          .payload(payload)
                          .messageId(messageId)
                          .queueName(queueName)
                          .groupId(groupId != null ? groupId : MessageHeaderConstants.Default.DEFAULT_GROUP_ID)
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
  public void publishDeadLetter(final DlqMessage message) {
    Objects.requireNonNull(message, "Dlq message can't be null");

    try {
      jmsTemplate.send(
          deadLettersQueueName,
          session -> {
            Message jmsMessage = session.createTextMessage(message.getPayload());

            jmsMessage.setStringProperty(MessageHeaderConstants.MESSAGE_ID, message.getMessageId());
            jmsMessage.setStringProperty(MessageHeaderConstants.ERROR_MESSAGE, message.getErrorMessage());

            return jmsMessage;
          });

      log.info("Dead letter {} published to DLQ", message.getMessageId());
    } catch (Exception e) {
      log.error("Failed to publish dead letter {}", message.getMessageId(), e);
      throw new RuntimeException("Failed to publish dead letter", e);
    }
  }
}
