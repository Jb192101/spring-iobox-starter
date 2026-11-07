package com.example.testapp.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;

@OutboxEntity(queueName = "user-created")
@Getter
@RequiredArgsConstructor
public class TestEvent {

    private final int pages;

    private final String author;
}