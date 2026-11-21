package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract sealed class AbstractBroker
    permits KafkaBrokerStrategy,
        RabbitMQBrokerStrategy,
        ActiveMQBrokerStrategy,
        RedisBrokerStrategy {

  protected final String deadLettersQueueName;
}