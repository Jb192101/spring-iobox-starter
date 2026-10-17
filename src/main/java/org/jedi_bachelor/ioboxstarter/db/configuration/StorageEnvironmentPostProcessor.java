package org.jedi_bachelor.ioboxstarter.db.configuration;

import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.properties.Storages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

@Slf4j
public class StorageEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String EXCLUDE_CONFIGURATION_POSITION = "spring.autoconfigure.exclude";

    private static final String JPA_EXCLUDES = String.join(",",
            "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration",
            "org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration"
    );

    private static final String MONGO_EXCLUDES = String.join(",",
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
            "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
            "org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration"
    );

    private static final String CASSANDRA_EXCLUDES = String.join(",",
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
            "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
            "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
    );

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        String storage = environment.getProperty("iobox.storage", Storages.JPA.getStorageName());
        Properties props = new Properties();

        log.info("StorageEnvironmentPostProcessor: Selected storage = {}", storage);

        if (Storages.MONGO.getStorageName().equalsIgnoreCase(storage)) {
            props.setProperty(EXCLUDE_CONFIGURATION_POSITION, MONGO_EXCLUDES);
        } else if (Storages.CASSANDRA.getStorageName().equalsIgnoreCase(storage)) {
            props.setProperty(EXCLUDE_CONFIGURATION_POSITION, CASSANDRA_EXCLUDES);
        } else if (Storages.JPA.getStorageName().equalsIgnoreCase(storage)) {
            props.setProperty(EXCLUDE_CONFIGURATION_POSITION, JPA_EXCLUDES);
        } else {
            return;
        }

        environment.getPropertySources().addFirst(
                new PropertiesPropertySource("iobox-storage-excludes", props)
        );
    }
}