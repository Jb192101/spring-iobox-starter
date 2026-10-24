package org.jedi_bachelor.ioboxstarter.db.entity.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "dlq_messages")
@Getter
@Setter
public class MongoDlqMessageEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String messageId = UUID.randomUUID().toString();

    private String payload;

    private String errorMessage;

    private boolean published = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }
}
