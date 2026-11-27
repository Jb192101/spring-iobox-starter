package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;

public final class InboxMappingUtil {

  private InboxMappingUtil() {}

  public static InboxMappedMessage toDto(final InboxMessage entity) {
    if (entity == null) {
      return null;
    }
    return InboxMappedMessage.builder()
        .id(entity.getId())
        .messageId(entity.getMessageId())
        .queueName(entity.getQueueName())
        .groupId(entity.getGroupId())
        .status(entity.getStatus())
        .processedAt(entity.getProcessedAt())
        .errorMessage(entity.getErrorMessage())
        .retryCount(entity.getRetryCount())
        .createdAt(entity.getCreatedAt())
        .version(entity.getVersion())
        .payload(entity.getPayload())
        .build();
  }
}
