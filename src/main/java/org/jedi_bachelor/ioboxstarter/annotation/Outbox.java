package org.jedi_bachelor.ioboxstarter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Outbox {
    String topic();               // Kafka topic
    Class<?> payloadType();       // Тип сообщения
    String idExpression();        // SpEL выражение для ID
    int maxRetries() default 5;
    long retryDelay() default 5000;
}
