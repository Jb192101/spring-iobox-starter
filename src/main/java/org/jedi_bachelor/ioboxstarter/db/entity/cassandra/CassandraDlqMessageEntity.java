package org.jedi_bachelor.ioboxstarter.db.entity.cassandra;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("dlq_messages")
@Getter
@Setter
public class CassandraDlqMessageEntity {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String messageId = UUID.randomUUID().toString();

    private String payload;

    private String errorMessage;

    private boolean published = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    public void markAsPublished() {
        published = true;
        publishedAt = LocalDateTime.now();
    }
}
