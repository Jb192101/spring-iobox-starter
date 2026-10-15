package org.jedi_bachelor.ioboxstarter.db.entity.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "inbox_messages")
@Getter
@Setter
public class MongoInboxMessageEntity {

    @Id
    private String id;

    private String payload;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Indexed(unique = true)
    private String messageId = UUID.randomUUID().toString();

    @Indexed
    private String queueName;

    private String groupId = "default";

    private boolean processed = false;

    private LocalDateTime processedAt;

    private String errorMessage;

    private int retryCount = 0;

    @Version
    private Long version;

    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = LocalDateTime.now();
    }

    public void incrementRetries() {
        this.retryCount++;
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.incrementRetries();
    }
}
