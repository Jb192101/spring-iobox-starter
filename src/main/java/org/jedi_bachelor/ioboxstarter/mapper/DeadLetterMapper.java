package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.springframework.stereotype.Component;

@Component
public final class DeadLetterMapper implements Mapper<DlqMessage, DeadLetterMappedMessage> {

    @Override
    public DeadLetterMappedMessage toDto(DlqMessage entity) {
        DeadLetterMappedMessage message = new DeadLetterMappedMessage();

        message.setId(entity.getId());
        message.setMessageId(entity.getMessageId());
        message.setPayload(entity.getPayload());
        message.setPublished(entity.isPublished());
        message.setErrorMessage(entity.getErrorMessage());

        return message;
    }
}
