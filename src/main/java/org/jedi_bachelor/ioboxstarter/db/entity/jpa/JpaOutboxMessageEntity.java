package org.jedi_bachelor.ioboxstarter.db.entity.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jedi_bachelor.ioboxstarter.db.status.OutboxMessageStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_messages")
@Getter
@Setter
public class JpaOutboxMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId = UUID.randomUUID().toString();

    @Column(name = "queue_name", nullable = false)
    private String queueName;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OutboxMessageStatus status = OutboxMessageStatus.NEW;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "retry_count")
    private int retryCount = 0;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    @SuppressWarnings("UnusedDeclaration")
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
