package org.jedi_bachelor.ioboxstarter.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "outbox.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxScheduler {

    private final OutboxService outboxService;
    private final OutboxProperties properties;

    @Scheduled(fixedDelayString = "${outbox.scheduler.interval:5000}")
    public void processOutboxMessages() {
        if (!properties.isEnabled()) {
            return;
        }

        try {
            List<OutboxMessage> messages = outboxService.getUnpublishedMessages();

            if (messages.isEmpty()) {
                log.debug("No pending outbox messages");
                return;
            }

            log.info("Found {} pending outbox messages", messages.size());

            int processed = 0;
            for (OutboxMessage message : messages) {
                if (outboxService.processMessage(message)) {
                    processed++;
                }
            }

            log.info("Processed {} out of {} messages", processed, messages.size());

        } catch (Exception e) {
            log.error("Error in outbox scheduler", e);
        }
    }

    @Scheduled(cron = "${outbox.scheduler.cleanup-cron:0 0 3 * * *}")
    public void cleanupOldMessages() {
        if (!properties.isEnabled()) {
            return;
        }

        log.info("Starting outbox cleanup");

        outboxService.cleanupOldMessages();
        outboxService.cleanupFailedMessages();

        log.info("Outbox cleanup completed");
    }
}
