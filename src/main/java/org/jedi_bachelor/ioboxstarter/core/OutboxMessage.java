package org.jedi_bachelor.ioboxstarter.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String payload;          // JSON

    @Column(name = "topic")
    private String topic;

    @Column(name = "message_id")
    private String messageId;         // Для дедупликации

    @Column(name = "published")
    private boolean published = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "error_message")
    private String errorMessage;

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }
}
