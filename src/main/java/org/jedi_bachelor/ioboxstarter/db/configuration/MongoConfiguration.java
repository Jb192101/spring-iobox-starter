package org.jedi_bachelor.ioboxstarter.db.configuration;

import org.jedi_bachelor.ioboxstarter.db.adapter.mongo.*;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.mongo.MongoDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.mongo.MongoInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.mongo.MongoOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ConditionalOnProperty(
        name = "iobox.storage",
        havingValue = "mongo"
)
@EnableMongoRepositories(
        basePackages = "org.jedi_bachelor.ioboxstarter.db.repository.mongo"
)
public class MongoConfiguration {

    @Bean
    public PortOutboxRepository portMongoOutboxRepository(MongoOutboxRepository repo) {
        return new MongoOutboxAdapter(repo, new MongoOutboxMapper());
    }

    @Bean
    public PortInboxRepository portMongoInboxRepository(MongoInboxRepository repo) {
        return new MongoInboxAdapter(repo, new MongoInboxMapper());
    }

    @Bean
    public PortDlqRepository portMongoDlqRepository(MongoDlqRepository repo) {
        return new MongoDlqAdapter(repo, new MongoDlqMapper());
    }
}
