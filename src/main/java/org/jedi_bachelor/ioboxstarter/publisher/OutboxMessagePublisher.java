package org.jedi_bachelor.ioboxstarter.publisher;

import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;

@FunctionalInterface
public interface OutboxMessagePublisher {
    void publish(OutboxMessage message) throws Exception;
}
