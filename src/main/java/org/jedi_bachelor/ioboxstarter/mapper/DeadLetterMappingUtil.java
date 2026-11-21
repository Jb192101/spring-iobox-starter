package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;

public final class DeadLetterMappingUtil {

  private DeadLetterMappingUtil() {}

  public static DeadLetterMappedMessage toDto(final DlqMessage entity) {
    DeadLetterMappedMessage message = new DeadLetterMappedMessage();

    message.setId(entity.getId());
    message.setMessageId(entity.getMessageId());
    message.setPayload(entity.getPayload());
    message.setStatus(entity.getStatus());
    message.setErrorMessage(entity.getErrorMessage());

    return message;
  }
}
