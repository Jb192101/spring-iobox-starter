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
}
