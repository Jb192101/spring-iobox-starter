package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;

public final class DeadLetterMappingUtil {

  private DeadLetterMappingUtil() {}

  public static DeadLetterMappedMessage toDto(final DlqMessage entity) {
    if(entity == null) {
      return null;
    }
    return DeadLetterMappedMessage.builder()
            .id(entity.getId())
            .messageId(entity.getMessageId())
            .payload(entity.getPayload())
            .errorMessage(entity.getErrorMessage())
            .status(entity.getStatus())
            .build();
  }
}
