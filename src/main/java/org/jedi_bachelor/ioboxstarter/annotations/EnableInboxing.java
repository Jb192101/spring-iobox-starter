package org.jedi_bachelor.ioboxstarter.annotations;

import java.lang.annotation.*;
import org.jedi_bachelor.ioboxstarter.configuration.InboxAutoConfiguration;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(InboxAutoConfiguration.class)
public @interface EnableInboxing {

    boolean enabled() default true;
}