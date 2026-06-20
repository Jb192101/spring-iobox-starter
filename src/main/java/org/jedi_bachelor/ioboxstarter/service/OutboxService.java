package org.jedi_bachelor.ioboxstarter.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.configuration.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessagePublisher;
import org.jedi_bachelor.ioboxstarter.repository.OutboxRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService<T extends OutboxMessage> {
    private final OutboxRepository<T> outboxRepository;

    private final OutboxMessagePublisher<T> publisher;

    private final OutboxProperties properties;

    /**
     * Обработать одно сообщение
     *
     * @return true, если сообщение успешно отправлено
     */
    @Transactional
    public boolean processMessage(T message) {
        try {
            log.debug("Processing outbox message: {}", message.getId());

            // Отправляем в брокер сообщений
            publisher.publish(message);

            // Помечаем как опубликованное
            message.setPublished(true);
            message.setPublishedAt(LocalDateTime.now());
            outboxRepository.save(message);

            log.info("Outbox message {} published successfully", message.getId());
            return true;

        } catch (Exception e) {
            log.error("Error processing outbox message: {}", message.getId(), e);

            // Увеличиваем счётчик retry
            message.incrementRetryCount();

            if (message.getRetryCount() >= properties.getMaxRetries()) {
                this.outboxRepository.delete(message);

                log.error("Outbox message {} reached max retries, deleted from database", message.getId());
            } else {
                log.warn("Outbox message {} retry {} of {}",
                        message.getId(), message.getRetryCount(), this.properties.getMaxRetries());
            }

            this.outboxRepository.save(message);

            return false;
        }
    }

    /**
     * Получить все неопубликованные сообщения
     */
    @Transactional
    public List<T> getUnpublishedMessages() {
        if (this.properties.isDeduplicationEnabled()) {
            return this.outboxRepository.findLatestUnpublishedMessages();
        }

        return this.outboxRepository.findUnpublishedOrderByCreatedAtAsc();
    }

    /**
     * Удалить старые обработанные сообщения
     */
    @Transactional
    public void cleanupOldMessages() {
        LocalDateTime olderThan = LocalDateTime.now()
                .minus(properties.getRetentionDays(), ChronoUnit.DAYS);

        this.outboxRepository.deleteProcessedOlderThan(olderThan);

        log.info("Cleaned up old outbox messages older than {}", olderThan);
    }
}
