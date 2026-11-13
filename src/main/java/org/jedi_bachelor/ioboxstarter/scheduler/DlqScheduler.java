package org.jedi_bachelor.ioboxstarter.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.jedi_bachelor.ioboxstarter.service.DlqService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DlqScheduler {

    private final DlqProperties properties;

    private final DlqService dlqService;

    public DlqScheduler(DlqProperties properties, DlqService dlqService) {
        this.properties = properties;
        this.dlqService = dlqService;
    }

    @Scheduled(fixedDelayString = "${dlq.scheduler.interval:5000}")
    @SchedulerLock(
            name = "DlqScheduler_processDeadMessages",
            lockAtMostFor = "PT10S",
            lockAtLeastFor = "PT1S"
    )
    @SuppressWarnings("UnusedDeclaration")
    public void processDeadMessages() {
        if (!properties.getEnabled() || !properties.getScheduler().getEnabled()) {
            return;
        }

        try {
            List<DlqMessage> messages = dlqService.findUnpublishedMessages();

            if (messages.isEmpty()) {
                log.debug("No unprocessed DLQ messages");
                return;
            }

            log.info("Found {} unprocessed DLQ messages", messages.size());

            for (DlqMessage message : messages) {
                dlqService.process(message);
            }

            log.info("Dead letters was published to topic {}", properties.getDlqName());

        } catch (Exception e) {
            log.error("Error in DLQ scheduler", e);
        }
    }
}
