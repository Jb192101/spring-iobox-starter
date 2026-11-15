package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;
import org.jedi_bachelor.ioboxstarter.db.status.InboxMessageStatus;

import java.time.LocalDateTime;

@Data
public final class InboxMappedMessage {

    private String id;

    private String messageId;

    @JsonRawValue
    private String payload;

    private String groupId;

    private String errorMessage;

    private LocalDateTime createdAt;

    private InboxMessageStatus status;

    private LocalDateTime processedAt;

    private String queueName;

    private int retryCount;

    private Long version;
}
