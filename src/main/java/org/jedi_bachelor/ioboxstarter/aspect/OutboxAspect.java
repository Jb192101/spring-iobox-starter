package org.jedi_bachelor.ioboxstarter.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.jedi_bachelor.ioboxstarter.annotation.Outbox;
import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.repository.OutboxRepository;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class OutboxAspect {
    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

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

    private OutboxMessage createOutboxMessage(ProceedingJoinPoint joinPoint, Outbox outbox, Object result) {
        // Логика извлечения payload и messageId из результата/аргументов
        // Используем SpEL для idExpression
        // ...
        return null;
    }
}
