package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;

public final class OutboxMappingUtil {

  private OutboxMappingUtil() {}

  public static OutboxMappedMessage toDto(final OutboxMessage entity) {
    if (entity == null) {
      return null;
    }

    OutboxMappedMessage dto = new OutboxMappedMessage();
    dto.setId(entity.getId());
    dto.setMessageId(entity.getMessageId());
    dto.setTopic(entity.getTopic());
    dto.setStatus(entity.getStatus());
    dto.setPublishedAt(entity.getPublishedAt());
    dto.setErrorMessage(entity.getErrorMessage());
    dto.setRetryCount(entity.getRetryCount());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setVersion(entity.getVersion());
    dto.setPayload(entity.getPayload());

    return dto;
  }
}
