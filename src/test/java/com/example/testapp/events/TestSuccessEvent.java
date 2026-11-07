package com.example.testapp.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;

@OutboxEntity(queueName = "test-success")
@Getter
@RequiredArgsConstructor
public class TestSuccessEvent {

    private final String message;
}