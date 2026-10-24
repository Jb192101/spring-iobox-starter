package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;

@Getter
@Setter
@AllArgsConstructor
sealed abstract public class AbstractBroker
        permits KafkaBrokerStrategy, RabbitMQBrokerStrategy, ActiveMQBrokerStrategy, RedisBrokerStrategy {

    protected DlqProperties dlqProperties;
}
