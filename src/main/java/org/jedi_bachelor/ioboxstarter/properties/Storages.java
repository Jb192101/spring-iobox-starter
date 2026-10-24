package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Storages {
    JPA("jpa"),

    MONGO("mongo"),

    CASSANDRA("cassandra");

    private final String storageName;

    public String getStorageName() {
        return storageName;
    }

    public static Set<String> getStorageNames() {
        return Set.of(Arrays.stream(
                Storages.values()).map(t -> t.getStorageName())
                .collect(Collectors.joining())
        );
    }

    public static Storages fromValue(String value) {
        if(!getStorageNames().contains(value))
            throw new IllegalStateException("Unexpected value: " + value);

        return switch (value) {
            case "jpa" -> Storages.JPA;
            case "cassandra" -> Storages.CASSANDRA;
            case "mongo" -> Storages.MONGO;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}
