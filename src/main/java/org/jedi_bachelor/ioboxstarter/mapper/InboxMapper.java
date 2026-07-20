package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboxMapper implements Mapper<InboxMessage, InboxMappedMessage> {
    @Override
    public InboxMappedMessage toDto(InboxMessage entity) {
        if (entity == null) {
            return null;
        }

        InboxMappedMessage dto = new InboxMappedMessage();
        dto.setId(entity.getId());
        dto.setMessageId(entity.getMessageId());
        dto.setQueueName(entity.getQueueName());
        dto.setGroupId(entity.getGroupId());
        dto.setProcessed(entity.isProcessed());
        dto.setProcessedAt(entity.getProcessedAt());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setRetryCount(entity.getRetryCount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setVersion(entity.getVersion());
        dto.setPayload(entity.getPayload());

        return dto;
    }
}
