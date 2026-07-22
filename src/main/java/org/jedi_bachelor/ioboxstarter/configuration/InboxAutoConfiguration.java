package org.jedi_bachelor.ioboxstarter.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerContext;
import org.jedi_bachelor.ioboxstarter.repository.DeadLettersRepository;
import org.jedi_bachelor.ioboxstarter.repository.InboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jedi_bachelor.ioboxstarter.properties.InboxProperties;
import org.jedi_bachelor.ioboxstarter.registry.InboxListenerRegistry;
import org.jedi_bachelor.ioboxstarter.service.InboxProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(InboxProperties.class)
@ComponentScan(basePackages = "org.jedi_bachelor.ioboxstarter")
@ConditionalOnProperty(name = "inbox.enabled", havingValue = "true", matchIfMissing = true)
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
            InboxRepository repository,
            ObjectMapper objectMapper,
            InboxProperties properties,
            DeadLettersRepository deadLettersRepository,
            BrokerContext brokerContext) {
        return new InboxProcessor(repository, objectMapper, properties, deadLettersRepository, registry, brokerContext);
    }
}
