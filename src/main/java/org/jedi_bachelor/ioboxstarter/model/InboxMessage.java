package org.jedi_bachelor.ioboxstarter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox_messages")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InboxMessage extends BaseInboxMessage {

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId = UUID.randomUUID().toString();

    @Column(name = "queue_name", nullable = false)
    private String queueName;

    @Column(name = "group_id")
    private String groupId = "default";

    @Column(name = "processed")
    private boolean processed = false;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Version
    @Column(name = "version")
    private Long version;

    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.retryCount++;
    }
}
