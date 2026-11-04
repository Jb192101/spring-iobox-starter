package org.jedi_bachelor.ioboxstarter.configuration.brokers;

import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.brokers.KafkaBrokerStrategy;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "kafka", matchIfMissing = true)
@EnableConfigurationProperties(DlqProperties.class)
public class KafkaConfiguration {

    @Bean
    @ConditionalOnMissingBean(BrokerStrategy.class)
    public BrokerStrategy kafkaBrokerStrategy(KafkaTemplate<String, Object> kafkaTemplate, DlqProperties dlqProperties) {
        return new KafkaBrokerStrategy(kafkaTemplate, dlqProperties);
    }
}
