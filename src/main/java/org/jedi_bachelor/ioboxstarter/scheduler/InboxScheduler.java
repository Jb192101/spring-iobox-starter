package org.jedi_bachelor.ioboxstarter.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.properties.InboxProperties;
import org.jedi_bachelor.ioboxstarter.service.InboxProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "inbox.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class InboxScheduler {

    private final InboxProcessor inboxProcessor;

    private final PortInboxRepository inboxRepository;

    private final InboxProperties properties;

    @Scheduled(fixedDelayString = "${inbox.scheduler.interval:5000}")
    public void processInboxMessages() {
        if (!properties.isEnabled()) {
            return;
        }

        try {
            List<InboxMessage> messages = inboxRepository.findUnprocessedMessages();

            if (messages.isEmpty()) {
                log.debug("No unprocessed inbox messages");
                return;
            }

            log.info("Found {} unprocessed inbox messages", messages.size());

            int processed = 0;
            for (InboxMessage message : messages) {
                if (inboxProcessor.process(message)) {
                    processed++;
                }
            }

            log.info("Processed {} out of {} messages", processed, messages.size());

        } catch (Exception e) {
            log.error("Error in inbox scheduler", e);
        }
    }

    @Scheduled(cron = "${inbox.scheduler.cleanup-cron:0 0 3 * * *}")
    public void cleanupOldMessages() {
        if (!properties.isEnabled()) {
            return;
        }

        log.info("Starting inbox cleanup");

        LocalDateTime olderThan = LocalDateTime.now()
                .minus(properties.getRetentionDays(), ChronoUnit.DAYS);

        inboxRepository.deleteProcessedOlderThan(olderThan);
        inboxRepository.deleteFailedMessages(properties.getMaxRetries());

        log.info("Inbox cleanup completed");
    }
}