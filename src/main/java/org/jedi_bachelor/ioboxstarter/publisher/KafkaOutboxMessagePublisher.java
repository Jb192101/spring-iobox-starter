package org.jedi_bachelor.ioboxstarter.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnClass(KafkaTemplate.class)
public class KafkaOutboxMessagePublisher implements OutboxMessagePublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(OutboxMessage message) {
        try {
            this.kafkaTemplate.send(message.getTopic(), message.getMessageId(), message.getPayload());

            log.debug("Message {} published to topic {}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("Failed to publish message {} to topic {}", message.getMessageId(), message.getTopic(), e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }
}
