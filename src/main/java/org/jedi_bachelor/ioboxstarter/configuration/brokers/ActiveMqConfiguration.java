package org.jedi_bachelor.ioboxstarter.configuration.brokers;

import org.jedi_bachelor.ioboxstarter.brokers.ActiveMQBrokerStrategy;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

@AutoConfiguration(before = org.springframework.boot.actuate.autoconfigure.jms.JmsHealthContributorAutoConfiguration.class)
@ConditionalOnClass(JmsTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "activemq")
@EnableConfigurationProperties(DlqProperties.class)
public class ActiveMqConfiguration {

    @Bean
    @ConditionalOnMissingBean(BrokerStrategy.class)
    public BrokerStrategy activeMqBrokerStrategy(JmsTemplate jmsTemplate, DlqProperties dlqProperties) {
        return new ActiveMQBrokerStrategy(jmsTemplate, dlqProperties);
    }
}
