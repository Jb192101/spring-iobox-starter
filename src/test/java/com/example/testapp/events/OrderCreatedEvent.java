package com.example.testapp.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;

@OutboxEntity(queueName = "order-created")
@Getter
@RequiredArgsConstructor
public class OrderCreatedEvent {

    private final Long orderId;

    private final double amount;
}