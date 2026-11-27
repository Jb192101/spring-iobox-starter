package org.jedi_bachelor.ioboxstarter.db.adapter.mongo;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoDlqMessageEntity;

public final class MongoDlqMappingUtil {

  private MongoDlqMappingUtil() {}

  public static MongoDlqMessageEntity toEntity(DlqMessage m) {
    MongoDlqMessageEntity e = new MongoDlqMessageEntity();
    e.setId(m.getId());
    e.setMessageId(m.getMessageId());
    e.setPayload(m.getPayload());
    e.setErrorMessage(m.getErrorMessage());
    e.setStatus(m.getStatus());
    e.setQueueName(m.getQueueName());
    e.setConsumerGroup(m.getConsumerGroup());
    e.setCreatedAt(m.getCreatedAt());
    e.setPublishedAt(m.getPublishedAt());
    return e;
  }

  public static DlqMessage toDomain(MongoDlqMessageEntity e) {
    DlqMessage m = new DlqMessage();
    m.setId(e.getId());
    m.setMessageId(e.getMessageId());
    m.setPayload(e.getPayload());
    m.setErrorMessage(e.getErrorMessage());
    m.setStatus(e.getStatus());
    m.setQueueName(e.getQueueName());
    m.setConsumerGroup(e.getConsumerGroup());
    m.setCreatedAt(e.getCreatedAt());
    m.setPublishedAt(e.getPublishedAt());
    return m;
  }
}
