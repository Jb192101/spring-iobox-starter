package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.time.LocalDateTime;

import lombok.Builder;
import org.jedi_bachelor.ioboxstarter.db.status.OutboxMessageStatus;

@Builder
public record OutboxMappedMessage(
        String id, String messageId, @JsonRawValue String payload, String topic, LocalDateTime createdAt,
        String errorMessage, OutboxMessageStatus status, LocalDateTime publishedAt,
        int retryCount, long version
) { }