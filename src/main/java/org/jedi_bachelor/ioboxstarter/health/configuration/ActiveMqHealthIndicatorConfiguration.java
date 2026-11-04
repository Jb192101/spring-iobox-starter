package org.jedi_bachelor.ioboxstarter.health.configuration;

import org.jedi_bachelor.ioboxstarter.health.ActiveMqHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

@AutoConfiguration
@ConditionalOnClass(JmsTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "activemq")
public class ActiveMqHealthIndicatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "activeMqHealthIndicator")
    @ConditionalOnProperty(name = "management.health.activemq.enabled", havingValue = "true", matchIfMissing = true)
    public HealthIndicator activeMqHealthIndicator(JmsTemplate jmsTemplate) {
        return new ActiveMqHealthIndicator(jmsTemplate);
    }
}
