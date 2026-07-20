package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InboxMappedMessage {
    private Long id;

    private String messageId;

    @JsonRawValue
    private String payload;

    private String groupId;

    private String errorMessage;

    private LocalDateTime createdAt;

    private boolean processed;

    private LocalDateTime processedAt;

    private String queueName;

    private int retryCount;

    private Long version;
}
