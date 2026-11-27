package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import java.util.UUID;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraDlqMessageEntity;

public final class CassandraDlqMappingUtil {

    private CassandraDlqMappingUtil() {}

    public static CassandraDlqMessageEntity toEntity(final DlqMessage m) {
        CassandraDlqMessageEntity e = new CassandraDlqMessageEntity();

        e.setId(m.getId() != null ? m.getId() : UUID.randomUUID().toString());
        e.setMessageId(m.getMessageId() != null ? m.getMessageId() : UUID.randomUUID().toString());
        e.setPayload(m.getPayload());
        e.setErrorMessage(m.getErrorMessage());
        e.setStatus(m.getStatus());
        e.setConsumerGroup(m.getConsumerGroup());
        e.setCreatedAt(m.getCreatedAt());
        e.setQueueName(m.getQueueName());
        e.setPublishedAt(m.getPublishedAt());

        return e;
    }

    public static DlqMessage toDomain(final CassandraDlqMessageEntity e) {
        DlqMessage m = new DlqMessage();

        m.setId(e.getId());
        m.setMessageId(e.getMessageId());
        m.setPayload(e.getPayload());
        m.setErrorMessage(e.getErrorMessage());
        m.setStatus(e.getStatus());
        m.setCreatedAt(e.getCreatedAt());
        m.setConsumerGroup(e.getConsumerGroup());
        m.setQueueName(e.getQueueName());
        m.setPublishedAt(e.getPublishedAt());

        return m;
    }
}
