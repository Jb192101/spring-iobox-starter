package org.jedi_bachelor.ioboxstarter.db.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.jedi_bachelor.ioboxstarter.db.status.InboxMessageStatus;

@Getter
@Setter
public final class InboxMessage {

  private String id;

  private String payload;

  private LocalDateTime createdAt = LocalDateTime.now();

  private String messageId = UUID.randomUUID().toString();

  private String queueName;

  private String groupId = "default";

  private InboxMessageStatus status = InboxMessageStatus.NEW;

  private LocalDateTime processedAt;

  private String errorMessage;

  private int retryCount = 0;

  private Long version;

  public boolean setStatus(InboxMessageStatus status) {
    if (InboxMessageStatus.hasNextStep(this.status, status)) {
      this.status = status;

      return true;
    } else {
      return false;
    }
  }

  public void markAsProcessed() {
    if (InboxMessageStatus.hasNextStep(status, InboxMessageStatus.PROCESSED)) {
      status = InboxMessageStatus.PROCESSED;
      processedAt = LocalDateTime.now();
    }
  }

  public void markAsFailed(String error) {
    if (InboxMessageStatus.hasNextStep(status, InboxMessageStatus.FAILED)) {
      status = InboxMessageStatus.FAILED;
      errorMessage = error;
      retryCount++;
    }
  }
}
