package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxMapper implements Mapper<OutboxMessage, OutboxMappedMessage> {
    @Override
    public OutboxMappedMessage toDto(OutboxMessage entity) {
        if (entity == null) {
            return null;
        }

        OutboxMappedMessage dto = new OutboxMappedMessage();
        dto.setId(entity.getId());
        dto.setMessageId(entity.getMessageId());
        dto.setTopic(entity.getTopic());
        dto.setPublished(entity.isPublished());
        dto.setPublishedAt(entity.getPublishedAt());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setRetryCount(entity.getRetryCount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setVersion(entity.getVersion());
        dto.setPayload(entity.getPayload());

        return dto;
    }
}
