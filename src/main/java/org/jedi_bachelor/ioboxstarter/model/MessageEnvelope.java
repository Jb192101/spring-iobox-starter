package org.jedi_bachelor.ioboxstarter.model;

import lombok.Builder;

@Builder
public record MessageEnvelope(
    String payload,
    String messageId,
    String queueName,
    String groupId,
    long timestamp,
    String fallbackQueueName) {}
