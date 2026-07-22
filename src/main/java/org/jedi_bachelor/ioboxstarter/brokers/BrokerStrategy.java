package org.jedi_bachelor.ioboxstarter.brokers;

import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;

import java.util.function.Consumer;

public interface BrokerStrategy {
    void publish(OutboxMessage message);

    void consume(String queueName, Consumer<MessageEnvelope> handler);

    void publishDeadLetter(DeadLettersEntity message);
}
