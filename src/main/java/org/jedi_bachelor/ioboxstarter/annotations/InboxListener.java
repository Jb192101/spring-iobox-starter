package org.jedi_bachelor.ioboxstarter.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface InboxListener {
    String queueName();
    String groupId() default "default";
}