package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Storages {
    JPA("jpa"),
    MONGO("mongo"),
    CASSANDRA("cassandra"),
    REDIS("redis");

    private final String storageName;

    public static Storages fromValue(String value) {
        if(value == null)
            throw new IllegalStateException("Unexpected value: " + value);

        return switch (value) {
            case "jpa" -> Storages.JPA;
            case "cassandra" -> Storages.CASSANDRA;
            case "mongo" -> Storages.MONGO;
            case "redis" -> Storages.REDIS;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}
