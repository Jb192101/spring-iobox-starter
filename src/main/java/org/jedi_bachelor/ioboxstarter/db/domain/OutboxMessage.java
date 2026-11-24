package org.jedi_bachelor.ioboxstarter.db.domain;

/*
 * Идея: существуют доменные entity, а есть персистентные entity (завязанные на Hibernate)
 */

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.jedi_bachelor.ioboxstarter.db.status.OutboxMessageStatus;

@Getter
@Setter
public final class OutboxMessage {

    private String id;

    private String messageId = UUID.randomUUID().toString();

    private String queueName;

    private String payload;

    private OutboxMessageStatus status = OutboxMessageStatus.NEW;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    private int retryCount;

    private String errorMessage;

    private Long version;

    public OutboxMessage() {}

    public OutboxMessage(String queueName, String payload) {
        this.queueName = queueName;
        this.payload = payload;
    }

    public boolean setStatus(OutboxMessageStatus status) {
        if(OutboxMessageStatus.hasNextStep(this.status, status)) {
            this.status = status;

            return true;
        } else {
            return false;
        }
    }

    public void markAsPublished() {
        if(OutboxMessageStatus.hasNextStep(status, OutboxMessageStatus.PROCESSED)) {
            status = OutboxMessageStatus.PROCESSED;
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void markAsFailed(String error) {
        if(OutboxMessageStatus.hasNextStep(status, OutboxMessageStatus.FAILED)) {
            status = OutboxMessageStatus.FAILED;
            this.errorMessage = error;
            this.retryCount++;
        }
    }
}
