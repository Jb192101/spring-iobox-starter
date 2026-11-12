package org.jedi_bachelor.ioboxstarter;

/*
 * OutboxContentManager used when starter configured to using Transactional Outbox.
 * It's facade, that hide all logic to work with Outbox itself, prepare data to OutboxService
 * Bean of this class created by Spring if 'iobox.outbox.enabled' set to true.
 *
 * @author Baryshev G.
 * @since 0.0.1
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;
import org.jedi_bachelor.ioboxstarter.service.OutboxService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

@Component
@Slf4j
public final class OutboxContextManager {

    /**
     * Mapper from object to string representation
     */
    private final ObjectMapper objectMapper;

    /**
     * Main service to work with Outbox in hidden section
     */
    private final OutboxService outboxService;

    public OutboxContextManager(OutboxService outboxService) {
        this.outboxService = outboxService;

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Main method to work with Transactional Outbox segment.
     * Extract @OutboxEntity annotation, taking queueName, convert fields of 'message' in payload and send queueName and payload in OutboxService
     * Check of using @Transactional in method where it was called
     *
     * @param message message with annotation @OutboxEntity, payload of that used in Outbox
     * @param <T> type of message
     */
    public <T> void save(@NotNull T message) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.warn("Method save() called outside transaction! This can lead to data inconsistencies");
        }

        Objects.requireNonNull(message, "Message can't be null");

        OutboxEntity annotation = message.getClass().getAnnotation(OutboxEntity.class);
        if (annotation == null) {
            log.warn("Message class {} is not annotated with @OutboxEntity", message.getClass().getName());
            return;
        }

        String topic = annotation.queueName();
        save(message, topic);
    }

    /**
     * Main method to work with Transactional Outbox segment.
     * Convert fields of 'message' in payload and send queueName and payload in OutboxService
     *
     * @param message message with annotation @OutboxEntity, payload of that used in Outbox
     * @param <T> type of message
     */
    public <T> void save(@NotNull T message, @NotNull final String topic) {
        Objects.requireNonNull(message, "Outbox message can't be null");
        Objects.requireNonNull(topic, "Queue name can't be null");

        try {
            String payload = objectMapper.writeValueAsString(message);

            outboxService.save(topic, payload);

            log.debug("Saved outbox message for topic: {}", topic);
        } catch (Exception e) {
            log.error("Failed to save outbox message", e);
            throw new RuntimeException("Failed to save outbox message", e);
        }
    }
}
