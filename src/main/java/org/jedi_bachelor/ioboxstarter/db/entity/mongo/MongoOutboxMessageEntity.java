package org.jedi_bachelor.ioboxstarter.db.entity.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "outbox_messages")
@Getter
@Setter
public class MongoOutboxMessageEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String messageId = UUID.randomUUID().toString();

    private String payload;

    private boolean published = false;

    @Indexed
    private String queueName;

    private String topic;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    private int retryCount = 0;

    private String errorMessage;

    @Version
    private Long version;

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void incrementRetries() { this.retryCount++; }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.incrementRetries();
    }
}