package org.jedi_bachelor.ioboxstarter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class OutboxService {

    private final PortOutboxRepository repository;

    private final PortDlqRepository deadLettersRepository;

    private final OutboxProperties properties;

    private final BrokerStrategy brokerStrategy;

    private final Counter outboxMessageCounter;

    public OutboxService(PortOutboxRepository repository,
                         PortDlqRepository deadLettersRepository,
                         OutboxProperties properties,
                         BrokerStrategy brokerStrategy,
                         MeterRegistry meterRegistry) {
        this.repository = repository;
        this.deadLettersRepository = deadLettersRepository;
        this.properties = properties;
        this.brokerStrategy = brokerStrategy;

        this.outboxMessageCounter = meterRegistry.counter("spring.iobox.outbox.message.amount");
    }

    @Transactional
    public OutboxMessage save(final String topic, final String payload) {
        OutboxMessage message = new OutboxMessage(topic, payload);

        return this.repository.save(message);
    }

    @Transactional
    public OutboxMessage save(final OutboxMessage message) {
        return this.repository.save(message);
    }

    @Transactional
    public boolean processMessage(final OutboxMessage message) {
        try {
            log.debug("Processing outbox message: {}", message.getMessageId());

            brokerStrategy.publish(message);
            message.markAsPublished();

            try {
                repository.save(message);
            } catch (OptimisticLockingFailureException e) {
                log.warn("Outbox message {} was already processed by another instance. Skipping.", message.getMessageId());
                return false;
            }

            outboxMessageCounter.increment();

            log.info("Outbox message {} published successfully", message.getMessageId());
            return true;

        } catch (Exception e) {
            log.error("Error publishing message {}", message.getMessageId(), e);

            message.markAsFailed(e.getMessage());

            if (message.getRetryCount() >= this.properties.getMaxRetries()) {
                DlqMessage deadLettersEntity = convertMessageToDeadLetter(message);

                deadLettersEntity.setErrorMessage(e.getMessage());
                deadLettersRepository.save(deadLettersEntity);
                brokerStrategy.publishDeadLetter(deadLettersEntity);

                this.repository.delete(message);
                log.error("Message {} reached max retries, sent to DLQ", message.getMessageId());
            } else {
                this.repository.save(message);
            }

            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<OutboxMessage> getUnpublishedMessages() {
        if (this.properties.getDeduplicationEnabled()) {
            return this.repository.findLatestUnpublishedMessages();
        }

        return this.repository.findUnpublishedOrderByCreatedAtAsc();
    }

    @Transactional
    public int cleanupOldMessages() {
        try {
            LocalDateTime olderThan = LocalDateTime.now()
                    .minus(properties.getRetentionDays(), ChronoUnit.DAYS);

            int deleted = repository.deleteProcessedOlderThan(olderThan);
            log.info("Deleted {} old outbox messages", deleted);
            return deleted;
        } catch (UnsupportedOperationException ex) {
            log.warn("DB type don't support cleanup of old messages. It will deleted by TTL.");
            return -1;
        }
    }

    @Transactional
    public int cleanupFailedMessages() {
        try {
            int deleted = this.repository.deleteFailedMessages(properties.getMaxRetries());
            log.info("Deleted {} failed outbox messages", deleted);
            return deleted;
        } catch (UnsupportedOperationException ex) {
            log.warn("DB type don't support cleanup of failed messages. It will deleted by TTL.");
            return -1;
        }
    }

    @Transactional(readOnly = true)
    public List<OutboxMessage> getAllMessages() {
        return repository.findAll();
    }

    private DlqMessage convertMessageToDeadLetter(final OutboxMessage outboxMessage) {
        DlqMessage entity = new DlqMessage();

        entity.setMessageId(outboxMessage.getMessageId());
        entity.setErrorMessage(outboxMessage.getErrorMessage());
        entity.setPayload(outboxMessage.getPayload());

        return entity;
    }
}