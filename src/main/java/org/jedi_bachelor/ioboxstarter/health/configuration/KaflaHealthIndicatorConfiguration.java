package org.jedi_bachelor.ioboxstarter.health.configuration;

import org.jedi_bachelor.ioboxstarter.health.KafkaHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "kafka")
public class KaflaHealthIndicatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "kafkaHealthIndicator")
    @ConditionalOnProperty(name = "management.health.kafka.enabled", havingValue = "true", matchIfMissing = true)
    public HealthIndicator kafkaHealthIndicator(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaHealthIndicator(kafkaTemplate);
    }
}
