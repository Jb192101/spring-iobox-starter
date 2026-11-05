package org.jedi_bachelor.ioboxstarter.configuration.db;

import org.jedi_bachelor.ioboxstarter.db.adapter.jpa.*;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaOutboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(
        name = "iobox.storage",
        havingValue = "jpa",
        matchIfMissing = true
)
@EnableJpaRepositories(
        basePackages = "org.jedi_bachelor.ioboxstarter.db.repository.jpa"
)
@ConditionalOnClass(JpaRepository.class)
public class JpaConfiguration {

    @Bean
    public PortOutboxRepository portJpaOutboxRepository(JpaOutboxRepository repo) {
        return new JpaOutboxAdapter(repo, new JpaOutboxMapper());
    }

    @Bean
    public PortInboxRepository portJpaInboxRepository(JpaInboxRepository repo) {
        return new JpaInboxAdapter(repo, new JpaInboxMapper());
    }

    @Bean
    public PortDlqRepository portJpaDlqRepository(JpaDlqRepository repo) {
        return new JpaDlqAdapter(repo, new JpaDlqMapper());
    }
}
