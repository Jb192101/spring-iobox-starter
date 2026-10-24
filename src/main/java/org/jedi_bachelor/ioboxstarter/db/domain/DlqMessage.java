package org.jedi_bachelor.ioboxstarter.db.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public final class DlqMessage {

    private String id;

    private String messageId = UUID.randomUUID().toString();

    private String payload;

    private String errorMessage;

    private boolean published = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    public static DlqMessage from(OutboxMessage outbox) {
        DlqMessage d = new DlqMessage();
        d.setMessageId(outbox.getMessageId());
        d.setPayload(outbox.getPayload());
        d.setErrorMessage(outbox.getErrorMessage());
        return d;
    }

    public void markAsPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }
}
