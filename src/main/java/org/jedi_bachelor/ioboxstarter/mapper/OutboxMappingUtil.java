package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;

public final class OutboxMappingUtil {

  private OutboxMappingUtil() {}

  public static OutboxMappedMessage toDto(final OutboxMessage entity) {
    if (entity == null) {
      return null;
    }
    return OutboxMappedMessage.builder()
        .id(entity.getId())
        .messageId(entity.getMessageId())
        .topic(entity.getQueueName())
        .status(entity.getStatus())
        .publishedAt(entity.getPublishedAt())
        .errorMessage(entity.getErrorMessage())
        .retryCount(entity.getRetryCount())
        .createdAt(entity.getCreatedAt())
        .version(entity.getVersion())
        .payload(entity.getPayload())
        .build();
  }
}
