package org.jedi_bachelor.ioboxstarter.db.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.jedi_bachelor.ioboxstarter.db.status.DeadLetterStatus;

@Getter
@Setter
public final class DlqMessage {

    private String id;

    private String messageId = UUID.randomUUID().toString();

    private String payload;

    private String errorMessage;

    private String queueName;

    private String consumerGroup;

    private DeadLetterStatus status = DeadLetterStatus.NEW;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    public boolean setStatus(DeadLetterStatus status) {
        if(DeadLetterStatus.hasNextStep(this.status, status)) {
            this.status = status;

            return true;
        } else {
            return false;
        }
    }

    public boolean markAsPublished() {
        if(DeadLetterStatus.hasNextStep(status, DeadLetterStatus.PUBLISHED)) {
            status = DeadLetterStatus.PUBLISHED;
            publishedAt = LocalDateTime.now();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "DlqMessage{" +
                "id='" + id + '\'' +
                ", messageId='" + messageId + '\'' +
                ", payload='" + payload + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", queueName='" + queueName + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", publishedAt=" + publishedAt +
                '}';
    }
}
