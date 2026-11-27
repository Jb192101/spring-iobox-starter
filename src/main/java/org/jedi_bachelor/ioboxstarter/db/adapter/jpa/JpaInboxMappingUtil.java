package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.jpa.JpaInboxMessageEntity;

public final class JpaInboxMappingUtil {

  private JpaInboxMappingUtil() {}

  public static JpaInboxMessageEntity toEntity(InboxMessage m) {
    JpaInboxMessageEntity e = new JpaInboxMessageEntity();
    e.setId(m.getId());
    e.setPayload(m.getPayload());
    e.setCreatedAt(m.getCreatedAt());
    e.setMessageId(m.getMessageId());
    e.setQueueName(m.getQueueName());
    e.setGroupId(m.getGroupId());
    e.setStatus(m.getStatus());
    e.setProcessedAt(m.getProcessedAt());
    e.setErrorMessage(m.getErrorMessage());
    e.setRetryCount(m.getRetryCount());
    e.setVersion(m.getVersion());
    return e;
  }

  public static InboxMessage toDomain(JpaInboxMessageEntity e) {
    InboxMessage m = new InboxMessage();
    m.setId(e.getId());
    m.setPayload(e.getPayload());
    m.setCreatedAt(e.getCreatedAt());
    m.setMessageId(e.getMessageId());
    m.setQueueName(e.getQueueName());
    m.setGroupId(e.getGroupId());
    m.setStatus(e.getStatus());
    m.setProcessedAt(e.getProcessedAt());
    m.setErrorMessage(e.getErrorMessage());
    m.setRetryCount(e.getRetryCount());
    m.setVersion(e.getVersion());
    return m;
  }
}
