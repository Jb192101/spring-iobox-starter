package org.jedi_bachelor.ioboxstarter.model;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public record InboxListenerMethod(
        Object bean, Method method,
        Class<?> parameterType, String queueName, String groupId) {
}
