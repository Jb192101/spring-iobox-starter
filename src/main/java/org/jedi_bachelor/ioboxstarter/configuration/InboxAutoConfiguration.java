package org.jedi_bachelor.ioboxstarter.configuration;

import org.jedi_bachelor.ioboxstarter.repository.InboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public InboxProcessor inboxProcessor(
            InboxListenerRegistry registry,
            InboxRepository repository,
            ObjectMapper objectMapper,
            InboxProperties properties) {
        return new InboxProcessor(repository, registry, objectMapper, properties);
    }
}
