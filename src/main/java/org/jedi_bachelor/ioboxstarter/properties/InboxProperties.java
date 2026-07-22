package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "inbox")
public class InboxProperties {
    private Brokers broker = Brokers.KAFKA;

    private boolean enabled = true;

    private int maxRetries = 5;

    private boolean deduplicationEnabled = true;

    private int retentionDays = 7;

    private Scheduler scheduler = new Scheduler();

    @Data
    public static class Scheduler {
        private boolean enabled = true;
        private long interval = 5000;
        private String cleanupCron = "0 0 3 * * *";
    }
}
