package org.jedi_bachelor.ioboxstarter.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessagePublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOutboxPublisher implements OutboxMessagePublisher<OutboxMessage> {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public void publish(OutboxMessage message) {
        try {
            log.debug("Publishing message to topic: {}, messageId: {}",
                    message.getTopic(), message.getMessageId());

            this.kafkaTemplate.send(
                    message.getTopic(),
                    message.getMessageId(),
                    message.getPayload()
            );

            log.info("Message published to topic: {}, messageId: {}",
                    message.getTopic(), message.getMessageId());

        } catch (Exception e) {
            log.error("Failed to publish message: {}", message.getId(), e);
            throw new RuntimeException("Kafka publish failed", e);
        }
    }
}
