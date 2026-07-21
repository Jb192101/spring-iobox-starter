package org.jedi_bachelor.ioboxstarter.mapper;

import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterMapper implements Mapper<DeadLettersEntity, DeadLetterMappedMessage> {
    @Override
    public DeadLetterMappedMessage toDto(DeadLettersEntity entity) {
        DeadLetterMappedMessage message = new DeadLetterMappedMessage();

        message.setId(entity.getId());
        message.setMessageId(entity.getMessageId());
        message.setPayload(entity.getPayload());
        message.setPublished(entity.isPublished());
        message.setErrorMessage(entity.getErrorMessage());

        return message;
    }
}
