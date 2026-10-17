package org.jedi_bachelor.ioboxstarter.db.entity.cassandra;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("outbox_messages")
@Getter
@Setter
public class CassandraOutboxMessageEntity {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String messageId = UUID.randomUUID().toString();

    private String queueName;

    private String payload;

    private boolean published = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    private String topic;

    private int retryCount = 0;

    private String errorMessage;

    private Long version;

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.incrementRetries();
    }

    public void incrementRetries() {
        this.retryCount++;
    }
}
