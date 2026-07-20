package org.jedi_bachelor.ioboxstarter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OutboxContextManager {
    private final ObjectMapper objectMapper;

    private final OutboxService outboxService;

    public OutboxContextManager(OutboxService outboxService) {
        this.outboxService = outboxService;

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T> void save(T message) {
        OutboxEntity annotation = message.getClass().getAnnotation(OutboxEntity.class);
        if (annotation == null) {
            log.warn("Message class {} is not annotated with @OutboxEntity", message.getClass().getName());
            return;
        }

        try {
            String topic = annotation.queueName();
            String payload = this.objectMapper.writeValueAsString(message);

            this.outboxService.save(topic, payload);

            log.debug("Saved outbox message for topic: {}", topic);

        } catch (Exception e) {
            log.error("Failed to save outbox message", e);
            throw new RuntimeException("Failed to save outbox message", e);
        }
    }

    public <T> void save(T message, String topic) {
        try {
            String payload = this.objectMapper.writeValueAsString(message);

            this.outboxService.save(topic, payload);

            log.debug("Saved outbox message for topic: {}", topic);
        } catch (Exception e) {
            log.error("Failed to save outbox message", e);
            throw new RuntimeException("Failed to save outbox message", e);
        }
    }
}
