package org.jedi_bachelor.ioboxstarter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.InboxListenerMethod;
import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.jedi_bachelor.ioboxstarter.properties.InboxProperties;
import org.jedi_bachelor.ioboxstarter.registry.InboxListenerRegistry;
import org.jedi_bachelor.ioboxstarter.repository.InboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxProcessor {
    private final InboxRepository repository;

    private final InboxListenerRegistry registry;

    private final ObjectMapper objectMapper;

    private final InboxProperties properties;

    @Transactional
    public boolean process(InboxMessage message) {
        try {
            log.debug("Processing inbox message: {}, queue: {}",
                    message.getMessageId(), message.getQueueName());

            List<InboxListenerMethod> listeners = this.registry.getListeners(message.getQueueName());

            if (listeners.isEmpty()) {
                log.warn("No handlers found for queue: {}", message.getQueueName());
                message.markAsProcessed();
                this.repository.save(message);
                return false;
            }

            for (InboxListenerMethod listener : listeners) {
                try {
                    this.invokeListener(listener, message);
                } catch (Exception e) {
                    log.error("Error in listener {}.{}",
                            listener.getBean().getClass().getSimpleName(),
                            listener.getMethod().getName(), e);
                    throw e;
                }
            }

            message.markAsProcessed();
            this.repository.save(message);

            log.info("Inbox message {} processed successfully", message.getMessageId());
            return true;

        } catch (Exception e) {
            log.error("Error processing inbox message {}", message.getMessageId(), e);

            message.markAsFailed(e.getMessage());

            if (message.getRetryCount() >= this.properties.getMaxRetries()) {
                this.repository.delete(message);

                log.error("Message {} reached max retries, deleted", message.getMessageId());
            } else {
                this.repository.save(message);
            }

            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void invokeListener(InboxListenerMethod listener, InboxMessage message) throws Exception {
        Object bean = listener.getBean();
        Method method = listener.getMethod();
        Class<?> parameterType = listener.getParameterType();

        Object payload = this.objectMapper.readValue(message.getPayload(), parameterType);

        method.invoke(bean, payload);

        log.debug("Listener {}.{} invoked successfully",
                bean.getClass().getSimpleName(), method.getName());
    }
}
