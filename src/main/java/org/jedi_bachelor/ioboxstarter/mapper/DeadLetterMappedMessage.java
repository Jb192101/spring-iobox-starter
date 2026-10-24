package org.jedi_bachelor.ioboxstarter.mapper;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

@Data
public final class DeadLetterMappedMessage {
    private String id;

    private String messageId;

    @JsonRawValue
    private String payload;

    private String errorMessage;

    private boolean published;
}
