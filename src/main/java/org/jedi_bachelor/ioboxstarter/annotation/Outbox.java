package org.jedi_bachelor.ioboxstarter.annotation;

import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Outbox {
    /**
     * Kafka-топик для отправки сообщения
     */
    String topic();

    /**
     * SpEL-выражение для извлечения идентификатора сообщения
     * Пример: "#bookId", "#result.id", "#args[0]"
     */
    String idExpression();

    /**
     * SpEL-выражение для извлечения тела сообщения
     * Пример: "#result", "#args[0]"
     */
    String payloadExpression();

    /**
     * Тип payload'а для десериализации
     */
    Class<?> payloadType();

    /**
     * Тип сообщения (наследник OutboxMessage)
     */
    Class<? extends OutboxMessage> messageType() default OutboxMessage.class;

    /**
     * Максимальное количество повторных попыток
     */
    int maxRetries() default 5;

    /**
     * Задержка между retry в миллисекундах
     */
    long retryDelay() default 5000;
}
