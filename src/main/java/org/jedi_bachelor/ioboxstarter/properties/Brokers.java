package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Brokers {

    KAFKA("kafka"),

    RABBIT("rabbit"),

    ACTIVEMQ("activemq"),

    REDIS("redis");

    private final String brokerName;

    public static Brokers fromValue(final String value) {
        if(value == null)
            throw new IllegalStateException("Unexpected value: " + value);

        return switch (value) {
            case "kafka" -> Brokers.KAFKA;
            case "rabbit" -> Brokers.RABBIT;
            case "activemq" -> Brokers.ACTIVEMQ;
            case "redis" -> Brokers.REDIS;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}
