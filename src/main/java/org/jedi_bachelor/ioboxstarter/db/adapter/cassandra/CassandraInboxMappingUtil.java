package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import java.util.UUID;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraInboxMessageEntity;

public final class CassandraInboxMappingUtil {

    private CassandraInboxMappingUtil() {}

    public static CassandraInboxMessageEntity toEntity(final InboxMessage m) {
        CassandraInboxMessageEntity e = new CassandraInboxMessageEntity();

        e.setId(m.getId() != null ? m.getId() : UUID.randomUUID().toString());
        e.setMessageId(m.getMessageId() != null ? m.getMessageId() : UUID.randomUUID().toString());

        e.setPayload(m.getPayload());
        e.setCreatedAt(m.getCreatedAt());
        e.setQueueName(m.getQueueName());
        e.setGroupId(m.getGroupId());
        e.setStatus(m.getStatus());
        e.setProcessedAt(m.getProcessedAt());
        e.setErrorMessage(m.getErrorMessage());
        e.setRetryCount(m.getRetryCount());
        e.setVersion(m.getVersion());

        return e;
    }

    public static InboxMessage toDomain(final CassandraInboxMessageEntity e) {
        InboxMessage m = new InboxMessage();

        m.setId(e.getId());
        m.setPayload(e.getPayload());
        m.setCreatedAt(e.getCreatedAt());
        m.setMessageId(e.getMessageId());
        m.setQueueName(e.getQueueName());
        m.setGroupId(e.getGroupId());
        m.setStatus(e.getStatus());
        m.setProcessedAt(e.getProcessedAt());
        m.setErrorMessage(e.getErrorMessage());
        m.setRetryCount(e.getRetryCount());
        m.setVersion(e.getVersion());

        return m;
    }
}
