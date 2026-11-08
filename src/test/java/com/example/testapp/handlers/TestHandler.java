package com.example.testapp.handlers;

import com.example.testapp.events.InvalidTestEvent;
import com.example.testapp.events.OrderCreatedEvent;
import com.example.testapp.events.TestEvent;
import com.example.testapp.events.TestSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.annotations.InboxListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TestHandler {

    @InboxListener(queueName = "user-created")
    public void handle(TestEvent event) {
        log.info(String.valueOf(event));
    }

    @InboxListener(queueName = "user-created")
    public void handle(InvalidTestEvent event) {
        throw new RuntimeException();
    }

    @InboxListener(queueName = "order-created")
    public void handle2(OrderCreatedEvent event) {
        log.info(String.valueOf(event));
    }

    @InboxListener(queueName = "test-success")
    public void handle3(TestSuccessEvent event) {
        log.info(String.valueOf(event));
    }
}