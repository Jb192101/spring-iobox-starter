package com.example.testapp.service;

import com.example.testapp.events.OrderCreatedEvent;
import com.example.testapp.events.TestEvent;
import com.example.testapp.events.TestSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.OutboxContextManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final OutboxContextManager outboxContextManager;

    public void addBook(TestEvent event) {
        log.info("Book added");

        outboxContextManager.save(event);
    }

    public boolean verify(TestEvent event) {
        return event.getPages() < 1000;
    }

    public void createOrder(Long orderId, double amount) {
        log.info("Creating order: {}", orderId);
        outboxContextManager.save(new OrderCreatedEvent(orderId, amount));
    }

    public void sendSuccessMessage(String text) {
        log.info("Sending success message");
        outboxContextManager.save(new TestSuccessEvent(text));
    }
}