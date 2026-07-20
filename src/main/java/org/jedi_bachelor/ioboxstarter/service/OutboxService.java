package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.publisher.OutboxMessagePublisher;
import org.jedi_bachelor.ioboxstarter.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {
    private final OutboxRepository repository;

    private final OutboxProperties properties;

    private final OutboxMessagePublisher publisher;

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

            this.publisher.publish(message);
            message.markAsPublished();
            this.repository.save(message);

            log.info("Outbox message {} published successfully", message.getMessageId());
            return true;

        } catch (Exception e) {
            log.error("Error publishing message {}", message.getMessageId(), e);

            message.markAsFailed(e.getMessage());

            if (message.getRetryCount() >= this.properties.getMaxRetries()) {
                this.repository.delete(message);

                log.error("Message {} reached max retries, deleted", message.getMessageId());
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

    @Transactional
    public List<OutboxMessage> getAllMessages() {
        return this.repository.findAll();
    }
}