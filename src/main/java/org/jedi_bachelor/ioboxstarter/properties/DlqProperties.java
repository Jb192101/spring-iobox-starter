package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "dlq")
public class DlqProperties {
    private Brokers broker = Brokers.KAFKA;

    private boolean enabled;

    private String dlqName;

    private Scheduler scheduler = new Scheduler();

    @Data
    public static class Scheduler {
        private boolean enabled = true;

        private long interval = 5000;
    }
}
