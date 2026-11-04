package org.jedi_bachelor.ioboxstarter.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;

public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory connectionFactory;

    public RedisHealthIndicator(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Health health() {
        try (var connection = connectionFactory.getConnection()) {
            connection.ping();
            return Health.up().withDetail("broker", "Redis").build();
        } catch (Exception e) {
            return Health.down().withDetail("broker", "Redis").withException(e).build();
        }
    }
}