package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;

public final class InboxMappingUtil {

  private InboxMappingUtil() {}

  public static InboxMappedMessage toDto(final InboxMessage entity) {
    if (entity == null) {
      return null;
    }

    InboxMappedMessage dto = new InboxMappedMessage();
    dto.setId(entity.getId());
    dto.setMessageId(entity.getMessageId());
    dto.setQueueName(entity.getQueueName());
    dto.setGroupId(entity.getGroupId());
    dto.setStatus(entity.getStatus());
    dto.setProcessedAt(entity.getProcessedAt());
    dto.setErrorMessage(entity.getErrorMessage());
    dto.setRetryCount(entity.getRetryCount());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setVersion(entity.getVersion());
    dto.setPayload(entity.getPayload());

    return dto;
  }
}
