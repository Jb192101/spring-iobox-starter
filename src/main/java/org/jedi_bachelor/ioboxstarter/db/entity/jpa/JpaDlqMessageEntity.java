package org.jedi_bachelor.ioboxstarter.db.entity.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dlq_message")
@Getter
@Setter
public class JpaDlqMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId = UUID.randomUUID().toString();

    @Column(name = "payload")
    private String payload;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "queue_name")
    private String queueName;

    @Column(name = "consumer_group")
    private String consumerGroup;

    @Column(name = "published")
    private boolean published = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    @SuppressWarnings("UnusedDeclaration")
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
