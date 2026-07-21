package org.jedi_bachelor.ioboxstarter.model.dlq;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_dead_letters")
@Data
public class DeadLettersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "payload")
    private String payload;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "published")
    private boolean published = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }
}
