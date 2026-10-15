package org.jedi_bachelor.ioboxstarter.db.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class InboxMessage {

    private String id;

    private String payload;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String messageId = UUID.randomUUID().toString();

    private String queueName;

    private String groupId = "default";

    private boolean processed = false;

    private LocalDateTime processedAt;

    private String errorMessage;

    private int retryCount = 0;

    private Long version;

    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String error) {
        this.errorMessage = error;
        this.retryCount++;
    }
}
