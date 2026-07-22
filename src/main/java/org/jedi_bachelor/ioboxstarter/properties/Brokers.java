package org.jedi_bachelor.ioboxstarter.properties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Brokers {
    KAFKA("kafka"),
    RABBIT("rabbit");

    private final String broker;

    public static Brokers fromValue(String value) {
        if(value == null)
            throw new IllegalStateException("Unexpected value: " + value);

        return switch (value) {
            case "kafka" -> Brokers.KAFKA;
            case "rabbit" -> Brokers.RABBIT;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }
}
