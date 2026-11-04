package org.jedi_bachelor.ioboxstarter.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jms.core.JmsTemplate;

public class ActiveMqHealthIndicator implements HealthIndicator {

    private final JmsTemplate jmsTemplate;

    public ActiveMqHealthIndicator(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public Health health() {
        try {
            jmsTemplate.getConnectionFactory().createConnection().close();
            return Health.up().withDetail("broker", "ActiveMQ").build();
        } catch (Exception e) {
            return Health.down().withDetail("broker", "ActiveMQ").withException(e).build();
        }
    }
}
