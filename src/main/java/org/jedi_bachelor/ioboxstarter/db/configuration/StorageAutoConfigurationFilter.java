package org.jedi_bachelor.ioboxstarter.db.configuration;

import lombok.Setter;
import org.jedi_bachelor.ioboxstarter.properties.Storages;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StorageAutoConfigurationFilter
        implements AutoConfigurationImportFilter, EnvironmentAware {

    @Setter
    private Environment environment;

    private static final Set<String> JPA_AUTOCONFIGS = new HashSet<>(Arrays.asList(
            "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
    ));

    private static final Set<String> MONGO_AUTOCONFIGS = new HashSet<>(Arrays.asList(
            "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
    ));

    @Override
    public boolean[] match(String[] autoConfigurationClasses,
                           AutoConfigurationMetadata autoConfigurationMetadata) {

        String storage = environment.getProperty("iobox.storage", Storages.JPA.getStorageName());
        boolean[] matches = new boolean[autoConfigurationClasses.length];

        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            String cls = autoConfigurationClasses[i];

            if (Storages.JPA.getStorageName().equalsIgnoreCase(storage)) {
                matches[i] = !MONGO_AUTOCONFIGS.contains(cls);
            } else if (Storages.MONGO.getStorageName().equalsIgnoreCase(storage)) {
                matches[i] = !JPA_AUTOCONFIGS.contains(cls);
            } else {
                matches[i] = true;
            }

        }
        return matches;
    }
}
