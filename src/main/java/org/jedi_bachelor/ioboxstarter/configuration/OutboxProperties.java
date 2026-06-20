package org.jedi_bachelor.ioboxstarter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "outbox")
public class OutboxProperties {
    private boolean enabled = true;

    private int maxRetries = 5;

    private long retryDelay = 5000;

    private boolean deduplicationEnabled = true;

    private int retentionDays = 7;  // Хранить 7 дней

    private Scheduler scheduler = new Scheduler();

    @Data
    public static class Scheduler {
        private long interval = 5000;  // 5 секунд
        private String cleanupCron = "0 0 3 * * *";  // Каждую ночь в 3:00
    }
}
