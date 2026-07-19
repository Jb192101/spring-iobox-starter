package org.jedi_bachelor.ioboxstarter.annotations;

import org.jedi_bachelor.ioboxstarter.configuration.OutboxAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(OutboxAutoConfiguration.class)
public @interface EnableOutboxing {
    boolean enable() default true;
}