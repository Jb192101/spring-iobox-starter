package org.jedi_bachelor.ioboxstarter.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxService outboxService;

    private final OutboxProperties properties;

    private ExecutorService threadPoolExecutor;

    @PostConstruct
    @SuppressWarnings("UnusedDeclaration")
    public void postConstruct() {
        this.threadPoolExecutor = Executors.newFixedThreadPool(properties.getScheduler().getConcurrencyLevel());
    }

    @Scheduled(fixedDelayString = "${outbox.scheduler.interval:5000}")
    @SchedulerLock(
            name = "OutboxScheduler_processOutboxMessages",
            lockAtMostFor = "PT30S",
            lockAtLeastFor = "PT1S"
    )
    @SuppressWarnings("UnusedDeclaration")
    public void processOutboxMessages() {
        if (!this.properties.getEnabled()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            List<OutboxMessage> messages = this.outboxService.getUnpublishedMessages();

            if (messages.isEmpty()) {
                log.debug("No pending outbox messages");
                return;
            }

            log.info("Found {} pending outbox messages", messages.size());

            Map<Integer, List<OutboxMessage>> buckets = messages.stream()
                    .collect(Collectors.groupingBy(
                            m -> Math.abs(m.getTopic().hashCode()) % properties.getScheduler().getConcurrencyLevel()));

            final AtomicInteger processed = new AtomicInteger(0);
            List<Future<?>> futures = new ArrayList<>();

            buckets.values().forEach(
                    bucket -> futures.add(threadPoolExecutor.submit(() -> {
                        for (OutboxMessage msg : bucket) {
                            if (outboxService.processMessage(msg)) {
                                processed.incrementAndGet();
                            }
                        }
                    }))
            );

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    log.error("Error while waiting for task completion", e);
                }
            }

            log.info("Processed {} out of {} messages", processed, messages.size());

        } catch (Exception e) {
            log.error("Error in outbox scheduler", e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Outbox processing cycle took {} ms", duration);
        }
    }

    @Scheduled(cron = "${outbox.scheduler.cleanup-cron:0 0 3 * * *}")
    @SuppressWarnings("UnusedDeclaration")
    public void cleanupOldMessages() {
        if (!this.properties.getEnabled()) {
            return;
        }

        log.info("Starting outbox cleanup");

        this.outboxService.cleanupOldMessages();
        this.outboxService.cleanupFailedMessages();

        log.info("Outbox cleanup completed");
    }

    @PreDestroy
    @SuppressWarnings("UnusedDeclaration")
    public void preDestroy() {
        if (this.threadPoolExecutor != null) {
            this.threadPoolExecutor.shutdown();
            try {
                if (!this.threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    this.threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.threadPoolExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
