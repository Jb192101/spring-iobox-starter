package org.jedi_bachelor.ioboxstarter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class BaseOutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId = UUID.randomUUID().toString();

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "published")
    private boolean published = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Version
    @Column(name = "version")
    private Long version;

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.incrementRetryCount();
    }
}