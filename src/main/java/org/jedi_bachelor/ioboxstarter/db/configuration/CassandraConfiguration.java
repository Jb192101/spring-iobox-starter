package org.jedi_bachelor.ioboxstarter.db.configuration;

import org.jedi_bachelor.ioboxstarter.db.adapter.cassandra.*;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraOutboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@ConditionalOnProperty(
        name = "iobox.storage",
        havingValue = "cassandra"
)
@EnableCassandraRepositories(
        basePackages = "org.jedi_bachelor.ioboxstarter.db.repository.cassandra"
)
public class CassandraConfiguration {

    @Bean
    public PortOutboxRepository portCassandraOutboxRepository(CassandraOutboxRepository repository) {
        return new CassandraOutboxAdapter(repository, new CassandraOutboxMapper());
    }

    @Bean
    public PortInboxRepository portCassandraInboxRepository(CassandraInboxRepository repository) {
        return new CassandraInboxAdapter(repository, new CassandraInboxMapper());
    }

    @Bean
    public PortDlqRepository portCassandraDlqRepository(CassandraDlqRepository repository) {
        return new CassandraDlqAdapter(repository, new CassandraDlqMapper());
    }
}
