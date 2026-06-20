package org.jedi_bachelor.ioboxstarter.core;

public interface OutboxMessagePublisher<T extends OutboxMessage> {
    void publish(T message);
    void retry(T message);
    void markAsDead(T message);
}
