package org.jedi_bachelor.ioboxstarter.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class BaseInboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}