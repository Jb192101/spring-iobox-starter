package org.jedi_bachelor.ioboxstarter.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@Getter
@RequiredArgsConstructor
public class InboxListenerMethod {
    private final Object bean;
    private final Method method;
    private final Class<?> parameterType;
    private final String queueName;
    private final String groupId;
}
