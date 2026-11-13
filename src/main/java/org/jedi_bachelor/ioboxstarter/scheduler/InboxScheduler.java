package org.jedi_bachelor.ioboxstarter.scheduler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.properties.InboxProperties;
import org.jedi_bachelor.ioboxstarter.service.InboxService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
@Slf4j
public class InboxScheduler {

    private final InboxService inboxService;

    private final InboxProperties properties;

    private final Counter inboxProcessedCounter;

    private final ExecutorService threadPoolExecutor;

    public InboxScheduler(InboxService inboxService, InboxProperties properties, MeterRegistry meterRegistry) {
        this.inboxService = inboxService;
        this.properties = properties;

        this.inboxProcessedCounter = meterRegistry.counter("spring.iobox.inbox.message.amount");

        this.threadPoolExecutor = Executors.newFixedThreadPool(properties.getScheduler().getConcurrencyLevel());
    }

    @Scheduled(fixedDelayString = "${inbox.scheduler.interval:5000}")
    @SchedulerLock(
            name = "InboxScheduler_processInboxMessages",
            lockAtMostFor = "PT10S",
            lockAtLeastFor = "PT1S"
    )
    @SuppressWarnings("UnusedDeclaration")
    public void processInboxMessages() {
        if (!properties.getEnabled() || !properties.getScheduler().getEnabled()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            List<InboxMessage> messages = inboxService.findUnprocessedMessages();

            if (messages.isEmpty()) {
                log.debug("No unprocessed inbox messages");
                return;
            }

            Map<Integer, List<InboxMessage>> buckets = messages.stream()
                            .collect(Collectors.groupingBy(m -> Math.abs(m.getQueueName().hashCode() % properties.getScheduler().getConcurrencyLevel())));

            log.info("Found {} unprocessed inbox messages", messages.size());

            final AtomicInteger processed = new AtomicInteger(0);
            List<Future<?>> futures = new ArrayList<>();

            buckets.values().forEach(bucket -> futures.add(threadPoolExecutor.submit(() -> {
                        for (InboxMessage message : bucket) {
                            if (inboxService.process(message)) {
                                processed.incrementAndGet();
                                inboxProcessedCounter.increment();
                            }
                        }
                    })
                )
            );

            for(Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    log.error("Error while waiting for task completion", e);
                }
            }

            log.info("Processed {} out of {} messages", processed, messages.size());

        } catch (Exception e) {
            log.error("Error in inbox scheduler", e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Inbox processing cycle took {} ms", duration);
        }
    }

    @Scheduled(cron = "${inbox.scheduler.cleanup-cron:0 0 3 * * *}")
    @SuppressWarnings("UnusedDeclaration")
    public void cleanupOldMessages() {
        if (!properties.getEnabled() || !properties.getScheduler().getEnabled()) {
            return;
        }

        log.info("Starting inbox cleanup");

        LocalDateTime olderThan = LocalDateTime.now().minusDays(properties.getRetentionDays());

        inboxService.deleteProcessedOlderThan(olderThan);
        inboxService.deleteFailedMessages(properties.getMaxRetries());

        log.info("Inbox cleanup completed");
    }

    @PreDestroy
    @SuppressWarnings("UnusedDeclaration")
    public void preDestroy() {
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