package org.jedi_bachelor.ioboxstarter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageEnvelope {
    private String payload;
    private String messageId;
    private String queueName;
    private String groupId;
    private Long timestamp;
    private String fallbackQueueName;
}
