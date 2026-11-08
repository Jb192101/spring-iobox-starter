package org.jedi_bachelor.ioboxstarter.configuration.health;

import org.jedi_bachelor.ioboxstarter.health.RedisHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfiguration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "redis")
public class RedisHealthIndicatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "redisHealthIndicator")
    @ConditionalOnProperty(name = "management.health.redis.enabled", havingValue = "true", matchIfMissing = true)
    public HealthIndicator redisHealthIndicator(RedisConnectionFactory connectionFactory) {
        return new RedisHealthIndicator(connectionFactory);
    }
}
