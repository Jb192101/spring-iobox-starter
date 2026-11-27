package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.time.LocalDateTime;

import lombok.Builder;
import org.jedi_bachelor.ioboxstarter.db.status.InboxMessageStatus;

@Builder
public record InboxMappedMessage(
        String id, String messageId, @JsonRawValue String payload, String groupId,
        String errorMessage, LocalDateTime createdAt, InboxMessageStatus status,
        LocalDateTime processedAt, String queueName, int retryCount, long version
) { }