package org.jedi_bachelor.ioboxstarter.configuration.brokers;

import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.brokers.RabbitMQBrokerStrategy;
import org.jedi_bachelor.ioboxstarter.health.RabbitMqHealthIndicator;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "rabbit")
@EnableConfigurationProperties(DlqProperties.class)
public class RabbitMqConfiguration {

    @Bean
    @ConditionalOnMissingBean(BrokerStrategy.class)
    public BrokerStrategy rabbitMqBrokerStrategy(RabbitTemplate rabbitTemplate, DlqProperties dlqProperties) {
        return new RabbitMQBrokerStrategy(rabbitTemplate, dlqProperties);
    }
}
