package org.jedi_bachelor.ioboxstarter.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface OutboxEntity {
    String topic();
}
