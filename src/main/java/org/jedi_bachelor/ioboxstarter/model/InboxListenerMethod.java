package org.jedi_bachelor.ioboxstarter.model;

import java.lang.reflect.Method;

public record InboxListenerMethod(
        Object bean, Method method,
        Class<?> parameterType, String queueName, String groupId) {
}
