package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerContext;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.repository.DeadLettersRepository;
import org.jedi_bachelor.ioboxstarter.repository.OutboxRepository;
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
    private final OutboxRepository repository;

    private final DeadLettersRepository deadLettersRepository;

    private final OutboxProperties properties;

    private final BrokerContext brokerContext;

    @Transactional
    public OutboxMessage save(String topic, String payload) {
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
                DeadLettersEntity deadLettersEntity = convertMessageToDeadLetter(message);
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
        LocalDateTime olderThan = LocalDateTime.now()
                .minus(this.properties.getRetentionDays(), ChronoUnit.DAYS);

        int deleted = this.repository.deleteProcessedOlderThan(olderThan);
        log.info("Deleted {} old outbox messages", deleted);
        return deleted;
    }

    @Transactional
    public int cleanupFailedMessages() {
        int deleted = this.repository.deleteFailedMessages(this.properties.getMaxRetries());
        log.info("Deleted {} failed outbox messages", deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    public List<OutboxMessage> getAllMessages() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<OutboxMessage> getMessageById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DeadLettersEntity> getAllDeadLetters() {
        return this.deadLettersRepository.findAll();
    }

    private DeadLettersEntity convertMessageToDeadLetter(OutboxMessage outboxMessage) {
        DeadLettersEntity entity = new DeadLettersEntity();
        entity.setMessageId(outboxMessage.getMessageId());
        entity.setErrorMessage(outboxMessage.getErrorMessage());
        entity.setPayload(outboxMessage.getPayload());
        return entity;
    }
}