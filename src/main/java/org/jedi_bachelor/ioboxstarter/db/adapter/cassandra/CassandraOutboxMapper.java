package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraOutboxMessageEntity;

public final class CassandraOutboxMapper {

    public CassandraOutboxMessageEntity toEntity(OutboxMessage m) {
        CassandraOutboxMessageEntity e = new CassandraOutboxMessageEntity();

        e.setId(m.getId() != null ? m.getId() : java.util.UUID.randomUUID().toString());
        e.setMessageId(m.getMessageId() != null ? m.getMessageId() : java.util.UUID.randomUUID().toString());

        e.setQueueName(m.getQueueName());
        e.setPayload(m.getPayload());
        e.setPublished(m.isPublished());
        e.setCreatedAt(m.getCreatedAt());
        e.setPublishedAt(m.getPublishedAt());
        e.setRetryCount(m.getRetryCount());
        e.setErrorMessage(m.getErrorMessage());
        e.setVersion(m.getVersion());
        e.setTopic(m.getTopic());

        return e;
    }

    public OutboxMessage toDomain(CassandraOutboxMessageEntity e) {
        OutboxMessage m = new OutboxMessage();

        m.setId(e.getId());
        m.setMessageId(e.getMessageId());
        m.setQueueName(e.getQueueName());
        m.setPayload(e.getPayload());
        m.setPublished(e.isPublished());
        m.setCreatedAt(e.getCreatedAt());
        m.setPublishedAt(e.getPublishedAt());
        m.setRetryCount(e.getRetryCount());
        m.setErrorMessage(e.getErrorMessage());
        m.setVersion(e.getVersion());
        m.setTopic(e.getTopic());

        return m;
    }
}
