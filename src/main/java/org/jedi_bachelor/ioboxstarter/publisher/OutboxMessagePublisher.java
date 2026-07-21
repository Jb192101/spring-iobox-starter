package org.jedi_bachelor.ioboxstarter.publisher;

import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;

public interface OutboxMessagePublisher {
    void publish(OutboxMessage message) throws Exception;

    void publishDeadLetter(DeadLettersEntity message);
}
