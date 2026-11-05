package org.jedi_bachelor.ioboxstarter.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import org.jedi_bachelor.ioboxstarter.OutboxContextManager;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.controller.OutboxController;
import org.jedi_bachelor.ioboxstarter.configuration.db.CassandraConfiguration;
import org.jedi_bachelor.ioboxstarter.configuration.db.JpaConfiguration;
import org.jedi_bachelor.ioboxstarter.configuration.db.MongoConfiguration;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.mapper.OutboxMapper;
import org.jedi_bachelor.ioboxstarter.properties.OutboxProperties;
import org.jedi_bachelor.ioboxstarter.scheduler.OutboxScheduler;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
@Import({JpaConfiguration.class, CassandraConfiguration.class, MongoConfiguration.class})
@ConditionalOnProperty(
        name = "iobox.outbox.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class OutboxAutoConfiguration {

    @Bean
    @ConditionalOnExpression(
            "${outbox.context-manager.enabled:true} and " +
                    "('${outbox.context-manager.realisation-type:java}' == 'java')"
    )
    public OutboxContextManager outboxContextManager(OutboxService outboxService) {
        return new OutboxContextManager(outboxService);
    }

    @Bean
    public OutboxService outboxService(PortOutboxRepository repository,
                                       PortDlqRepository deadLettersRepository,
                                       OutboxProperties properties,
                                       BrokerStrategy brokerStrategy,
                                       MeterRegistry meterRegistry) {
        return new OutboxService(repository, deadLettersRepository, properties, brokerStrategy, meterRegistry);
    }

    @Bean
    public OutboxController outboxController(OutboxService outboxService, OutboxMapper outboxMapper) {
        return new OutboxController(outboxService, outboxMapper);
    }

    @Bean
    public OutboxScheduler outboxScheduler(OutboxService outboxService, OutboxProperties properties) {
        return new OutboxScheduler(outboxService, properties);
    }

    @Bean
    public OutboxMapper outboxMapper() {
        return new OutboxMapper();
    }
}