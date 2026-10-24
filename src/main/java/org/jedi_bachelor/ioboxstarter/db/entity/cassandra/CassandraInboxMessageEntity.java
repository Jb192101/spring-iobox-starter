package org.jedi_bachelor.ioboxstarter.db.entity.cassandra;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("inbox_messages")
@Getter
@Setter
public class CassandraInboxMessageEntity {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

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

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.incrementRetries();
    }

    public void incrementRetries() {
        this.retryCount++;
    }
}
