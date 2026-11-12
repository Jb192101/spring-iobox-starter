package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum Brokers {

    KAFKA("kafka"),

    RABBIT("rabbit"),

    ACTIVEMQ("activemq"),

    REDIS("redis");

    private final String brokerName;

    public static Brokers fromValue(final String value) {
        Objects.requireNonNull(value, "Value can't be null");

        return switch (value) {
            case "kafka" -> Brokers.KAFKA;
            case "rabbit" -> Brokers.RABBIT;
            case "activemq" -> Brokers.ACTIVEMQ;
            case "redis" -> Brokers.REDIS;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}
