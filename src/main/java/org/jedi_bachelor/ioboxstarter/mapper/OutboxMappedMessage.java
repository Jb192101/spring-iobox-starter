package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.time.LocalDateTime;
import lombok.Data;
import org.jedi_bachelor.ioboxstarter.db.status.OutboxMessageStatus;

@Data
public final class OutboxMappedMessage {

    private String id;

    private String messageId;

    @JsonRawValue
    private String payload;

    private String topic;

    private LocalDateTime createdAt;

    private String errorMessage;

    private OutboxMessageStatus status;

    private LocalDateTime publishedAt;

    private int retryCount;

    private Long version;
}
