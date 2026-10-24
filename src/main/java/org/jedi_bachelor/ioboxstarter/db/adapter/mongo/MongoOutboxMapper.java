package org.jedi_bachelor.ioboxstarter.db.adapter.mongo;

import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoOutboxMessageEntity;

public final class MongoOutboxMapper {

    public MongoOutboxMessageEntity toEntity(OutboxMessage m) {
        MongoOutboxMessageEntity e = new MongoOutboxMessageEntity();

        e.setId(m.getId());
        e.setMessageId(m.getMessageId());
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

    public OutboxMessage toDomain(MongoOutboxMessageEntity e) {
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

        return m;
    }
}
