package org.jedi_bachelor.ioboxstarter.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jedi_bachelor.ioboxstarter.annotation.Outbox;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.repository.OutboxRepository;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxAspect {
    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    private final SpelExpressionParser spelParser = new SpelExpressionParser();

    @Around("@annotation(outbox)")
    public Object handleOutbox(ProceedingJoinPoint joinPoint, Outbox outbox) throws Throwable {
        // Выполняем бизнес-логику
        Object result = joinPoint.proceed();

        // Создаём сообщение для Outbox
        OutboxMessage message = this.createOutboxMessage(joinPoint, outbox, result);

        // Сохраняем в БД
        this.outboxRepository.save(message);

        return result;
    }

    /**
     * Создание Outbox-сообщения из результата метода и аргументов
     */
    @SuppressWarnings("unchecked")
    private OutboxMessage createOutboxMessage(
            ProceedingJoinPoint joinPoint,
            Outbox outbox,
            Object result) {

        // Создаём экземпляр конкретного наследника через рефлексию
        OutboxMessage message = this.createMessageInstance(outbox);

        // Подготавливаем SpEL-контекст
        StandardEvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // Добавляем параметры метода как переменные
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        context.setVariable("result", result);

        // Извлекаем messageId через SpEL
        String messageId = this.spelParser.parseExpression(outbox.idExpression())
                .getValue(context, String.class);
        message.setMessageId(messageId);

        // Устанавливаем topic
        message.setTopic(outbox.topic());

        // Извлекаем payload через SpEL и сериализуем в JSON
        try {
            Object payload = this.spelParser.parseExpression(outbox.payloadExpression())
                    .getValue(context, outbox.payloadType());
            message.setPayload(this.objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            log.error("Failed to serialize payload for message: {}", messageId, e);
            throw new RuntimeException("Payload serialization failed", e);
        }

        return message;
    }

    /**
     * Создание экземпляра конкретного типа OutboxMessage
     */
    @SuppressWarnings("unchecked")
    private OutboxMessage createMessageInstance(Outbox outbox) {
        Class<? extends OutboxMessage> messageClass = outbox.messageType();

        try {
            // Проверяем, что это не абстрактный класс
            if (Modifier.isAbstract(messageClass.getModifiers())) {
                log.warn("Message class {} is abstract, using default implementation", messageClass.getName());
                return createDefaultMessageInstance();
            }

            // Создаём экземпляр через рефлексию
            Constructor<? extends OutboxMessage> constructor = messageClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();

        } catch (Exception e) {
            log.error("Failed to create instance of {}", messageClass.getName(), e);
            throw new RuntimeException("Cannot create OutboxMessage instance for: " + messageClass.getName(), e);
        }
    }

    /**
     * Создание экземпляра по умолчанию (через анонимный класс)
     */
    private OutboxMessage createDefaultMessageInstance() {
        return new OutboxMessage() {};
    }
}
