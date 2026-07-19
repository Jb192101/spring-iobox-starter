package org.jedi_bachelor.ioboxstarter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jedi_bachelor.ioboxstarter.OutboxContextManager;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
@ComponentScan(basePackages = "org.jedi_bachelor.ioboxstarter")
@ConditionalOnProperty(
        name = "outbox.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class OutboxAutoConfiguration {
    @Bean
    @ConditionalOnProperty(
            name = "outbox.context-manager.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public OutboxContextManager outboxContextManager(OutboxService outboxService) {
        return new OutboxContextManager(outboxService);
    }
}