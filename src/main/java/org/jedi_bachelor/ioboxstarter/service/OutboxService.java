package org.jedi_bachelor.ioboxstarter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.status.OutboxMessageStatus;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OutboxService {

  private final PortOutboxRepository repository;

  private final PortDlqRepository deadLettersRepository;

  private final OutboxProperties properties;

  private final DlqProperties dlqProperties;

  private final BrokerStrategy brokerStrategy;

  private final Counter outboxMessageCounter;

  public OutboxService(
      PortOutboxRepository repository,
      PortDlqRepository deadLettersRepository,
      OutboxProperties properties,
      BrokerStrategy brokerStrategy,
      MeterRegistry meterRegistry,
      DlqProperties dlqProperties) {
    this.repository = repository;
    this.deadLettersRepository = deadLettersRepository;
    this.properties = properties;
    this.brokerStrategy = brokerStrategy;
    this.dlqProperties = dlqProperties;

    this.outboxMessageCounter = meterRegistry.counter("spring.iobox.outbox.message.amount");
  }

  @Transactional
  public OutboxMessage save(final String topic, final String payload) {
    Objects.requireNonNull(payload, "Payload can't be null");
    Objects.requireNonNull(topic, "Topic can't be null");

    OutboxMessage message = new OutboxMessage(topic, payload);

    return repository.save(message);
  }

  @Transactional
  public OutboxMessage save(final OutboxMessage message) {
    return repository.save(message);
  }

  @Transactional
  public boolean processMessage(OutboxMessage message) {
    Objects.requireNonNull(message, "Message can't be null");

    try {
      log.debug("Processing outbox message: {}", message.getMessageId());

      if (!message.setStatus(OutboxMessageStatus.PENDING)) {
        log.warn(
            "Cannot transition DLQ message {} to PENDING, current status: {}",
            message.getId(),
            message.getStatus());
        return false;
      }

      brokerStrategy.publish(message);
      message.markAsPublished();

      try {
        repository.save(message);
      } catch (OptimisticLockingFailureException e) {
        log.warn(
            "Outbox message {} was already processed by another instance. Skipping.",
            message.getMessageId());
        return false;
      }

      outboxMessageCounter.increment();

      log.info("Outbox message {} published successfully", message.getMessageId());
      return true;

    } catch (Exception e) {
      log.error("Error publishing message {}", message.getMessageId(), e);

      message.markAsFailed(e.getMessage());

      if (message.getRetryCount() >= properties.getMaxRetries()) {
        message.setStatus(OutboxMessageStatus.COMPLETELY_FAILED);
        if (dlqProperties.getEnabled()) {
          DlqMessage deadLettersEntity = convertMessageToDeadLetter(message, e.getMessage());

          deadLettersRepository.save(deadLettersEntity);
          brokerStrategy.publishDeadLetter(deadLettersEntity);

          repository.delete(message);
          log.error("Message {} reached max retries, sent to DLQ", message.getMessageId());
        } else {
          log.error(
              "Message {} reached max retries, message marked as COMPLETELY_FAILED",
              message.getMessageId());
        }
      } else {
        repository.save(message);
      }

      return false;
    }
  }

  @Transactional(readOnly = true)
  public List<OutboxMessage> getUnpublishedMessages() {
    if (properties.getDeduplicationEnabled()) {
      return repository.findLatestUnpublishedMessages();
    }

    return repository.findUnpublishedOrderByCreatedAtAsc();
  }

  @Transactional
  public void cleanupOldMessages() {
    try {
      LocalDateTime olderThan = LocalDateTime.now().minusDays(properties.getRetentionDays());

      int deleted = repository.deleteProcessedOlderThan(olderThan);
      log.info("Deleted {} old outbox messages", deleted);
    } catch (UnsupportedOperationException ex) {
      log.warn("DB type don't support cleanup of old messages. It will deleted by TTL.");
    }
  }

  @Transactional
  public void cleanupFailedMessages() {
    try {
      int deleted = repository.deleteFailedMessages(properties.getMaxRetries());
      log.info("Deleted {} failed outbox messages", deleted);
    } catch (UnsupportedOperationException ex) {
      log.warn("DB type don't support cleanup of failed messages. It will deleted by TTL.");
    }
  }

  @Transactional(readOnly = true)
  public List<OutboxMessage> getAllMessages() {
    return repository.findAll();
  }

  private DlqMessage convertMessageToDeadLetter(
      final OutboxMessage outboxMessage, final String errorMessage) {
    DlqMessage entity = new DlqMessage();

    entity.setMessageId(outboxMessage.getMessageId());
    entity.setErrorMessage(outboxMessage.getErrorMessage());
    entity.setPayload(outboxMessage.getPayload());
    entity.setErrorMessage(errorMessage);

    return entity;
  }
}
