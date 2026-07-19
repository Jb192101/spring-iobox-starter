package org.jedi_bachelor.ioboxstarter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_messages")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OutboxMessage extends BaseOutboxMessage {
    public OutboxMessage(String topic, String payload) {
        this.setTopic(topic);
        this.setPayload(payload);
    }
}
