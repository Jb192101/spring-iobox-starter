package org.jedi_bachelor.ioboxstarter.brokers;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;

import java.util.function.Consumer;

sealed public interface BrokerStrategy
        permits KafkaBrokerStrategy, RabbitMQBrokerStrategy, ActiveMQBrokerStrategy, RedisBrokerStrategy {

    void publish(final OutboxMessage message);

    void consume(final String queueName, final Consumer<MessageEnvelope> handler);

    void publishDeadLetter(final DlqMessage message);
}
