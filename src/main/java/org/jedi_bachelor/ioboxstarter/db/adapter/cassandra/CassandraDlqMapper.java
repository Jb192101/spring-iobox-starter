package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraDlqMessageEntity;

public final class CassandraDlqMapper {

    public CassandraDlqMessageEntity toEntity(DlqMessage m) {
        CassandraDlqMessageEntity e = new CassandraDlqMessageEntity();

        e.setId(m.getId() != null ? m.getId() : java.util.UUID.randomUUID().toString());
        e.setMessageId(m.getMessageId() != null ? m.getMessageId() : java.util.UUID.randomUUID().toString());
        e.setPayload(m.getPayload());
        e.setErrorMessage(m.getErrorMessage());
        e.setPublished(m.isPublished());
        e.setCreatedAt(m.getCreatedAt());
        e.setPublishedAt(m.getPublishedAt());

        return e;
    }

    public DlqMessage toDomain(CassandraDlqMessageEntity e) {
        DlqMessage m = new DlqMessage();

        m.setId(e.getId());
        m.setMessageId(e.getMessageId());
        m.setPayload(e.getPayload());
        m.setErrorMessage(e.getErrorMessage());
        m.setPublished(e.isPublished());
        m.setCreatedAt(e.getCreatedAt());
        m.setPublishedAt(e.getPublishedAt());

        return m;
    }
}
