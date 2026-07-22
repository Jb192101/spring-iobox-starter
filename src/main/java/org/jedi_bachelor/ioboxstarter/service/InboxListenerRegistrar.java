package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.annotations.InboxListener;
import org.jedi_bachelor.ioboxstarter.model.InboxListenerMethod;
import org.jedi_bachelor.ioboxstarter.registry.InboxListenerRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboxListenerRegistrar implements BeanPostProcessor {
    private final InboxListenerRegistry registry;

    private final Map<String, List<InboxListenerMethod>> listenersByQueue = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        for (Method method : beanClass.getDeclaredMethods()) {
            InboxListener annotation = method.getAnnotation(InboxListener.class);
            if (annotation != null) {
                registerListener(bean, method, annotation);
            }
        }

        Class<?> superClass = beanClass.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            for (Method method : superClass.getDeclaredMethods()) {
                InboxListener annotation = method.getAnnotation(InboxListener.class);
                if (annotation != null) {
                    registerListener(bean, method, annotation);
                }
            }
            superClass = superClass.getSuperclass();
        }

        return bean;
    }

    private void registerListener(Object bean, Method method, InboxListener annotation) {
        method.setAccessible(true);

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            log.warn("InboxListener method {} must have exactly one parameter, found {} parameters",
                    method.getName(), parameterTypes.length);
            return;
        }

        InboxListenerMethod listenerMethod = new InboxListenerMethod(
                bean,
                method,
                parameterTypes[0],
                annotation.queueName(),
                annotation.groupId()
        );

        this.registry.register(annotation.queueName(), listenerMethod);
        log.info("Registered inbox listener for queue '{}': {}.{}",
                annotation.queueName(), bean.getClass().getSimpleName(), method.getName());
    }

    public Set<String> getAllQueues() {
        return Set.copyOf(this.listenersByQueue.keySet());
    }
}