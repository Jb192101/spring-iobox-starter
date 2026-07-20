package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OutboxMappedMessage {
    private Long id;

    private String messageId;

    @JsonRawValue
    private String payload;

    private String topic;

    private LocalDateTime createdAt;

    private String errorMessage;

    private boolean published;

    private LocalDateTime publishedAt;

    private int retryCount;

    private Long version;
}
