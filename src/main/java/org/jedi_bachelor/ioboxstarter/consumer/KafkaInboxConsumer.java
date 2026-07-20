package org.jedi_bachelor.ioboxstarter.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.jedi_bachelor.ioboxstarter.repository.InboxRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnExpression("'${inbox.kafka.topics:}'.trim().length() > 0")
public class KafkaInboxConsumer {
    private final InboxRepository inboxRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{'${inbox.kafka.topics}'.split(',')}")
    public void consume(
            String messageJson,
            @Header(name = "kafka_receivedTopic", required = false) String topic)
    {
        try {
            log.debug("Received message from Kafka");

            //InboxMessage message = this.objectMapper.readValue(messageJson, InboxMessage.class);
            InboxMessage message = new InboxMessage();
            message.setMessageId(UUID.randomUUID().toString());
            message.setPayload(messageJson);
            message.setQueueName(topic);

            if (this.inboxRepository.findByMessageId(message.getMessageId()).isPresent()) {
                log.info("Duplicate message, skipping: {}", message.getMessageId());
                return;
            }

            this.inboxRepository.save(message);
            log.info("Saved inbox message: {}. Body: {}", message.getMessageId(), message.toString());

        } catch (Exception e) {
            log.error("Failed to consume message", e);
        }
    }
}