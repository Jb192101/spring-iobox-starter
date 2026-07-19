package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {
    private boolean enabled = true;
    private int maxRetries = 5;
    private long retryDelay = 5000;
    private boolean deduplicationEnabled = true;
    private int retentionDays = 7;
    private Scheduler scheduler = new Scheduler();
    private ContextManager contextManager = new ContextManager();

    @Data
    public static class Scheduler {
        private long interval = 5000;
        private String cleanupCron = "0 0 3 * * *";
        private boolean enabled = true;
    }

    @Data
    public static class ContextManager {
        private boolean enabled = true;
        private String defaultTopic = "default";
    }
}