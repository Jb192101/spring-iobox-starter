package com.example.testapp.events;

/**
 * This DTO present invalid message for TestEvent class
 * In TestHandler implemented only processing of TestEvent messages from user-created topic
 */

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.annotations.OutboxEntity;

@OutboxEntity(queueName = "user-created")
@Getter
@RequiredArgsConstructor
public class InvalidTestEvent {

    private final int pages;
}
