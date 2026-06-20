package org.jedi_bachelor.ioboxstarter.configuration;

import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessagePublisher;
import org.jedi_bachelor.ioboxstarter.repository.OutboxRepository;
import org.jedi_bachelor.ioboxstarter.scheduler.OutboxScheduler;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnClass(OutboxMessage.class)
@EnableConfigurationProperties(OutboxProperties.class)
@EnableScheduling
public class OutboxAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public <T extends OutboxMessage> OutboxService<T> outboxService(
            OutboxRepository<T> outboxRepository,
            OutboxMessagePublisher<T> publisher,
            OutboxProperties properties) {

        return new OutboxService<>(outboxRepository, publisher, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public <T extends OutboxMessage> OutboxScheduler<T> outboxScheduler(
            OutboxService<T> outboxService,
            OutboxProperties properties) {
        return new OutboxScheduler<>(outboxService, properties);
    }
}
