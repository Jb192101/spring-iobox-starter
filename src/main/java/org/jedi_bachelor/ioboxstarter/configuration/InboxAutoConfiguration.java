package org.jedi_bachelor.ioboxstarter.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.micrometer.core.instrument.MeterRegistry;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.controller.InboxController;
import org.jedi_bachelor.ioboxstarter.configuration.db.CassandraConfiguration;
import org.jedi_bachelor.ioboxstarter.configuration.db.JpaConfiguration;
import org.jedi_bachelor.ioboxstarter.configuration.db.MongoConfiguration;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.mapper.InboxMapper;
import org.jedi_bachelor.ioboxstarter.scheduler.InboxScheduler;
import org.jedi_bachelor.ioboxstarter.service.InboxListenerRegistrar;
import org.jedi_bachelor.ioboxstarter.service.InboxService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jedi_bachelor.ioboxstarter.properties.InboxProperties;
import org.jedi_bachelor.ioboxstarter.registry.InboxListenerRegistry;
import org.jedi_bachelor.ioboxstarter.service.InboxProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(InboxProperties.class)
@Import({JpaConfiguration.class, CassandraConfiguration.class, MongoConfiguration.class})
@ConditionalOnProperty(name = "iobox.inbox.enabled", havingValue = "true", matchIfMissing = true)
public class InboxAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new ParameterNamesModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public InboxProcessor inboxProcessor(
            InboxListenerRegistry registry,
            PortInboxRepository repository,
            ObjectMapper objectMapper,
            InboxProperties properties,
            PortDlqRepository deadLettersRepository,
            BrokerStrategy brokerContext) {
        return new InboxProcessor(repository, objectMapper, properties, deadLettersRepository, registry, brokerContext);
    }

    @Bean
    public InboxListenerRegistry inboxListenerRegistry() {
        return new InboxListenerRegistry();
    }

    @Bean
    public InboxController inboxController(InboxService inboxService, InboxMapper inboxMapper) {
        return new InboxController(inboxService, inboxMapper);
    }

    @Bean
    public InboxMapper inboxMapper() {
        return new InboxMapper();
    }

    @Bean
    public InboxService inboxService(PortInboxRepository repository) {
        return new InboxService(repository);
    }

    @Bean
    public InboxScheduler inboxScheduler(InboxProcessor inboxProcessor,
                                         PortInboxRepository inboxRepository,
                                         InboxProperties properties,
                                         MeterRegistry meterRegistry
    ) {
        return new InboxScheduler(inboxProcessor, inboxRepository, properties, meterRegistry);
    }

    @Bean
    public static InboxListenerRegistrar inboxListenerRegistrar(InboxListenerRegistry registry) {
        return new InboxListenerRegistrar(registry);
    }
}
