package org.jedi_bachelor.ioboxstarter.health.configuration;

import org.jedi_bachelor.ioboxstarter.health.RabbitMqHealthIndicator;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "rabbit")
public class RabbitMqHealthIndicatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "rabbitMqHealthIndicator")
    @ConditionalOnProperty(name = "management.health.rabbit.enabled", havingValue = "true", matchIfMissing = true)
    public HealthIndicator rabbitMqHealthIndicator(RabbitTemplate rabbitTemplate) {
        return new RabbitMqHealthIndicator(rabbitTemplate);
    }
}
