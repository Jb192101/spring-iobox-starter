package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import org.jedi_bachelor.ioboxstarter.db.status.DeadLetterStatus;

@Builder
public record DeadLetterMappedMessage(
        String id, String messageId, @JsonRawValue String payload,
        String errorMessage, DeadLetterStatus status
) { }