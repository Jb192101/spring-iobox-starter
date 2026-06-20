package org.jedi_bachelor.ioboxstarter.core.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessagePublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaOutboxPublisher<T extends OutboxMessage> implements OutboxMessagePublisher<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(T message) {

    }

    @Override
    public void retry(T message) {

    }

    @Override
    public void markAsDead(T message) {

    }
}
