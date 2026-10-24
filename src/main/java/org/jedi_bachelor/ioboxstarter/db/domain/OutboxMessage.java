package org.jedi_bachelor.ioboxstarter.db.domain;

/**
 * Идея: существуют доменные entity, а есть персистентные entity (завязанные на Hibernate)
 */

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public final class OutboxMessage {

    private String id;

    private String messageId = UUID.randomUUID().toString();;

    private String queueName;

    private String payload;

    private boolean published;

    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    private int retryCount;

    private String errorMessage;

    private Long version;

    public OutboxMessage() {}

    public OutboxMessage(String queueName, String payload) {
        this.messageId = UUID.randomUUID().toString();
        this.queueName = queueName;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsFailed(String error) {
        this.errorMessage = error;
        this.retryCount++;
    }

    public void setTopic(String topic) {
        this.queueName = topic;
    }

    public String getTopic() {
        return queueName;
    }
}
