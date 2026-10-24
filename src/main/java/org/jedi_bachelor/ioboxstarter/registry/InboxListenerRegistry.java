package org.jedi_bachelor.ioboxstarter.registry;

import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.InboxListenerMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

@Component
@Slf4j
public final class InboxListenerRegistry {

    private final Map<String, List<InboxListenerMethod>> listenersByQueue = new ConcurrentHashMap<>();

    public void register(final String queueName, final InboxListenerMethod listenerMethod) {
        listenersByQueue.computeIfAbsent(queueName, k -> new ArrayList<>())
                .add(listenerMethod);

        log.debug("Registered inbox listener for queue: {}, method: {}",
                queueName, listenerMethod.getMethod().getName());
    }

    public List<InboxListenerMethod> getListeners(final String queueName) {
        return this.listenersByQueue.getOrDefault(queueName, List.of());
    }

    public boolean hasListeners(final String queueName) {
        return this.listenersByQueue.containsKey(queueName) &&
                !this.listenersByQueue.get(queueName).isEmpty();
    }

    public Map<String, List<InboxListenerMethod>> getAllListeners() {
        return Map.copyOf(this.listenersByQueue);
    }
}
