package org.jedi_bachelor.ioboxstarter.brokers;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;

import java.util.function.Consumer;

sealed public interface BrokerStrategy
        permits KafkaBrokerStrategy, RabbitMQBrokerStrategy, ActiveMQBrokerStrategy, RedisBrokerStrategy {

    void publish(OutboxMessage message);

    void consume(String queueName, Consumer<MessageEnvelope> handler);

    void publishDeadLetter(DlqMessage message);
}
