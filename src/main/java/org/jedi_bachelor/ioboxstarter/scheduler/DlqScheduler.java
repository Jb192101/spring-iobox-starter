package org.jedi_bachelor.ioboxstarter.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.jedi_bachelor.ioboxstarter.publisher.OutboxMessagePublisher;
import org.jedi_bachelor.ioboxstarter.repository.DeadLettersRepository;
import org.jedi_bachelor.ioboxstarter.service.DlqService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "dlq.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class DlqScheduler {
    private final DlqProperties properties;

    private final DeadLettersRepository repository;

    private final DlqService dlqService;

    @Scheduled(fixedDelayString = "${dlq.scheduler.interval:5000}")
    public void processDeadMessages() {
        if (!this.properties.isEnabled()) {
            return;
        }

        try {
            List<DeadLettersEntity> messages = this.repository.findUnpublishedMessages();

            if (messages.isEmpty()) {
                log.debug("No unprocessed DLQ messages");
                return;
            }

            log.info("Found {} unprocessed DLQ messages", messages.size());

            for (DeadLettersEntity message : messages) {
                this.dlqService.process(message);
            }

            log.info("Dead letters was published to topic {}", this.properties.getDlqName());

        } catch (Exception e) {
            log.error("Error in DLQ scheduler", e);
        }
    }
}
