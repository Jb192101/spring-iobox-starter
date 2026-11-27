package org.jedi_bachelor.ioboxstarter.annotations;

import java.lang.annotation.*;
import org.jedi_bachelor.ioboxstarter.configuration.OutboxAutoConfiguration;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(OutboxAutoConfiguration.class)
public @interface EnableOutboxing {

    boolean enabled() default true;
}