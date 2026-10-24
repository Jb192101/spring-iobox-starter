package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerContext;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final PortOutboxRepository repository;

    private final PortDlqRepository deadLettersRepository;

    private final OutboxProperties properties;

    private final BrokerContext brokerContext;

    @Transactional
    public OutboxMessage save(final String topic, final String payload) {
        OutboxMessage message = new OutboxMessage(topic, payload);

        return this.repository.save(message);
    }

    @Transactional
    public OutboxMessage save(OutboxMessage message) {
        return this.repository.save(message);
    }

    @Transactional
    public boolean processMessage(OutboxMessage message) {
        try {
            log.debug("Processing outbox message: {}", message.getMessageId());

            this.brokerContext.publish(message);
            message.markAsPublished();
            this.repository.save(message);

            log.info("Outbox message {} published successfully", message.getMessageId());
            return true;

        } catch (Exception e) {
            log.error("Error publishing message {}", message.getMessageId(), e);

            message.markAsFailed(e.getMessage());

            if (message.getRetryCount() >= this.properties.getMaxRetries()) {
                DlqMessage deadLettersEntity = convertMessageToDeadLetter(message);

                deadLettersEntity.setErrorMessage(e.getMessage());
                deadLettersRepository.save(deadLettersEntity);
                brokerContext.publishDeadLetter(deadLettersEntity);

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
        if (this.properties.isDeduplicationEnabled()) {
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

    @Transactional(readOnly = true)
    public List<DlqMessage> getAllDeadLetters() {
        return this.deadLettersRepository.findAll();
    }

    private DlqMessage convertMessageToDeadLetter(final OutboxMessage outboxMessage) {
        DlqMessage entity = new DlqMessage();

        entity.setMessageId(outboxMessage.getMessageId());
        entity.setErrorMessage(outboxMessage.getErrorMessage());
        entity.setPayload(outboxMessage.getPayload());

        return entity;
    }
}