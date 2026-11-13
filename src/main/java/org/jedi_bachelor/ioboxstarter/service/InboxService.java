package org.jedi_bachelor.ioboxstarter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.model.InboxListenerMethod;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.properties.InboxProperties;
import org.jedi_bachelor.ioboxstarter.registry.InboxListenerRegistry;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxService {

    private final PortInboxRepository repository;

    private final ObjectMapper objectMapper;

    private final InboxProperties properties;

    private final PortDlqRepository deadLettersRepository;

    private final InboxListenerRegistry registry;

    private final BrokerStrategy brokerStrategy;

    @PostConstruct
    @SuppressWarnings("UnusedDeclaration")
    public void init() {
        Map<String, List<InboxListenerMethod>> allListeners = registry.getAllListeners();

        if (allListeners.isEmpty()) {
            log.debug("No inbox listeners registered, skipping consumer initialization");
            return;
        }

        log.info("Initializing inbox consumers for {} queues", allListeners.size());

        for (Map.Entry<String, List<InboxListenerMethod>> entry : allListeners.entrySet()) {
            String queueName = entry.getKey();
            int handlerCount = entry.getValue().size();

            log.info("Starting inbox consumer for queue: {} ({} handlers)", queueName, handlerCount);
            brokerStrategy.consume(queueName, this::handleIncomingMessage);
        }

        log.info("Inbox consumers initialized successfully");
    }

    @Transactional
    public List<InboxMessage> getAllMessages() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<InboxMessage> findUnprocessedMessages() {
        return repository.findUnprocessedMessages();
    }

    @Transactional
    public void deleteProcessedOlderThan(LocalDateTime olderThan) {
        repository.deleteProcessedOlderThan(olderThan);
    }

    @Transactional
    public void deleteFailedMessages(int amount) {
        repository.deleteFailedMessages(amount);
    }

    /**
     * Обработчик входящих сообщений из брокера
     * Получает сообщение вместе с метаданными из заголовков
     */
    private void handleIncomingMessage(final MessageEnvelope envelope) {
        try {
            String payload = envelope.getPayload();
            String messageId = envelope.getMessageId();
            String queueName = envelope.getQueueName();
            String groupId = envelope.getGroupId();
            Long timestamp = envelope.getTimestamp();

            log.debug("Received message from queue: {}, id: {}", queueName, messageId);

            // Если messageId не пришел в заголовках - генерируем
            if (messageId == null || messageId.isEmpty()) {
                messageId = UUID.randomUUID().toString();
                log.debug("Generated new messageId: {}", messageId);
            }

            // Если queueName не пришел - используем переданный из контекста
            if (queueName == null || queueName.isEmpty()) {
                queueName = envelope.getFallbackQueueName();
                log.warn("No queueName in headers, using fallback: {}", queueName);
            }

            // Если groupId не пришел - используем default
            if (groupId == null || groupId.isEmpty()) {
                groupId = "default";
            }

            // Создаем InboxMessage
            InboxMessage message = new InboxMessage();
            message.setMessageId(messageId);
            message.setQueueName(queueName);
            message.setGroupId(groupId);
            message.setPayload(payload);

            if (timestamp != null) {
                message.setCreatedAt(LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(timestamp),
                        java.time.ZoneId.systemDefault()
                ));
            }

            // Проверка дедупликации
            if (this.properties.getDeduplicationEnabled()) {
                if (this.repository.findByMessageId(messageId).isPresent()) {
                    log.info("Duplicate message detected, skipping: {}", messageId);
                    return;
                }
            }

            // Сохраняем в Inbox
            this.repository.save(message);
            log.info("Saved inbox message: {} from queue: {}", messageId, queueName);

        } catch (Exception e) {
            log.error("Failed to process incoming message", e);
            // Отправляем в DLQ в случае ошибки
            try {
                InboxMessage fallbackMessage = new InboxMessage();
                fallbackMessage.setMessageId(UUID.randomUUID().toString());
                fallbackMessage.setQueueName(envelope.getFallbackQueueName());
                fallbackMessage.setPayload(envelope.getPayload());
                fallbackMessage.setErrorMessage("Failed to process incoming message: " + e.getMessage());

                this.sendToDeadLetter(fallbackMessage, "Failed to process incoming message: " + e.getMessage());
            } catch (Exception ex) {
                log.error("Failed to save message to DLQ", ex);
            }
        }
    }

    @Transactional(noRollbackFor = {Exception.class})
    public boolean process(final InboxMessage message) {
        try {
            log.debug("Processing inbox message: {}, queue: {}",
                    message.getMessageId(), message.getQueueName());

            List<InboxListenerMethod> listeners = this.registry.getListeners(message.getQueueName());

            if (listeners.isEmpty()) {
                log.warn("No handlers found for queue: {}", message.getQueueName());
                this.sendToDeadLetter(message, "No handlers found for queue: " + message.getQueueName());
                message.markAsProcessed();
                this.repository.save(message);
                return false;
            }

            Exception lastException = null;
            for (InboxListenerMethod listener : listeners) {
                try {
                    this.invokeListener(listener, message);
                    log.debug("Listener {}.{} executed successfully",
                            listener.bean().getClass().getSimpleName(),
                            listener.method().getName());
                } catch (Exception e) {
                    lastException = e;
                    log.error("Error in listener {}.{}",
                            listener.bean().getClass().getSimpleName(),
                            listener.method().getName(), e);
                }
            }

            if (lastException != null) {
                throw lastException;
            }

            message.markAsProcessed();

            try {
                repository.save(message);
            } catch (OptimisticLockingFailureException e) {
                log.warn("Inbox message {} was already processed by another instance. Skipping.", message.getMessageId());
                return false;
            }

            log.info("Inbox message {} processed successfully", message.getMessageId());
            return true;

        } catch (Exception e) {
            log.error("Error processing inbox message {}", message.getMessageId(), e);

            message.markAsFailed(e.getMessage());

            if (message.getRetryCount() >= this.properties.getMaxRetries()) {
                this.sendToDeadLetter(message, "Max retries (" + this.properties.getMaxRetries() +
                        ") reached: " + e.getMessage());
                this.repository.delete(message);
                log.error("Message {} reached max retries, sent to DLQ", message.getMessageId());
            } else {
                this.repository.save(message);
                log.info("Message {} will be retried (attempt {}/{})",
                        message.getMessageId(),
                        message.getRetryCount(),
                        this.properties.getMaxRetries());
            }

            return false;
        }
    }

    private void sendToDeadLetter(final InboxMessage message, final String errorMessage) {
        try {
            DlqMessage deadLetter = convertMessageToDeadLetter(message);
            deadLetter.setErrorMessage(errorMessage);

            this.deadLettersRepository.save(deadLetter);
            log.debug("Dead letter saved to database: {}", deadLetter.getMessageId());

            this.brokerStrategy.publishDeadLetter(deadLetter);

            log.info("Message {} sent to DLQ with error: {}", message.getMessageId(), errorMessage);

        } catch (Exception e) {
            log.error("Failed to send message to DLQ: {}", message.getMessageId(), e);
        }
    }

    private void invokeListener(final InboxListenerMethod listener, final InboxMessage message) throws Exception {
        Object bean = listener.bean();
        Method method = listener.method();
        Class<?> parameterType = listener.parameterType();

        Object payload;

        if (parameterType.isAssignableFrom(InboxMessage.class)) {
            payload = message;
        } else {
            try {
                JsonNode node = this.objectMapper.readTree(message.getPayload());
                payload = this.objectMapper.treeToValue(node, parameterType);
            } catch (Exception e) {
                log.error("Failed to deserialize payload: {}", message.getPayload(), e);
                throw new RuntimeException("Failed to deserialize payload to " + parameterType.getName(), e);
            }
        }

        method.invoke(bean, payload);

        log.debug("Listener {}.{} invoked successfully with parameter type: {}",
                bean.getClass().getSimpleName(),
                method.getName(),
                parameterType.getSimpleName());
    }

    private DlqMessage convertMessageToDeadLetter(final InboxMessage message) {
        DlqMessage entity = new DlqMessage();

        entity.setMessageId(message.getMessageId());
        entity.setPayload(message.getPayload());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setPublished(false);
        entity.setQueueName(message.getQueueName());
        entity.setConsumerGroup(message.getGroupId());

        return entity;
    }
}