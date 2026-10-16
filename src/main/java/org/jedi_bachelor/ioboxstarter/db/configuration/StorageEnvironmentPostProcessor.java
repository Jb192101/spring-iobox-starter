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
            "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
    );

    private static final String MONGO_EXCLUDES = String.join(",",
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
            "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
    );

    private static final String CASSANDRA_EXCLUDES =
            "org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration," +
                    "org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration," +
                    "org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration," +
                    "org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration," +
                    "org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        log.info("StorageEnvironmentPostProcessor has start work!");

        String storage = environment.getProperty("iobox.storage", "jpa");
        Properties props = new Properties();

        log.info("Choised DB configuration: {}", storage);

        if (Storages.MONGO.getStorageName().equalsIgnoreCase(storage)) {
            props.setProperty(EXCLUDE_CONFIGURATION_POSITION, MONGO_EXCLUDES + "," + CASSANDRA_EXCLUDES);
        } else if (Storages.JPA.getStorageName().equalsIgnoreCase(storage)) {
            props.setProperty(EXCLUDE_CONFIGURATION_POSITION, JPA_EXCLUDES + "," + CASSANDRA_EXCLUDES);
        } else {
            return;
        }

        environment.getPropertySources().addFirst(
                new PropertiesPropertySource("iobox-storage-excludes", props)
        );
    }
}