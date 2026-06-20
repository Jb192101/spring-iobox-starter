package org.jedi_bachelor.ioboxstarter.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.configuration.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler<T extends OutboxMessage> {
    private final OutboxService<T> outboxService;

    private final OutboxProperties properties;

    @Scheduled(fixedDelayString = "${outbox.scheduler.interval:5000}")
    public void processOutboxMessages() {
        log.debug("Outbox scheduler started");

        try {
            // Получаем все неопубликованные сообщения
            List<T> messages = this.outboxService.getUnpublishedMessages();

            if (messages.isEmpty()) {
                log.debug("No pending outbox messages");
                return;
            }

            log.info("Found {} pending outbox messages", messages.size());

            // Обрабатываем каждое сообщение
            for (T message : messages) {
                this.outboxService.processMessage(message);
            }

        } catch (Exception e) {
            log.error("Error in outbox scheduler", e);
        }
    }

    /**
     * Очистка старых сообщений (раз в день)
     */
    @Scheduled(cron = "${outbox.scheduler.cleanup-cron:0 0 3 * * *}")
    public void cleanupOldMessages() {
        log.info("Starting outbox cleanup");

        this.outboxService.cleanupOldMessages();

        log.info("Outbox cleanup completed");
    }
}
