package org.jedi_bachelor.ioboxstarter.db.adapter.mongo;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoDlqMessageEntity;

public final class MongoDlqMapper {

    public MongoDlqMessageEntity toEntity(DlqMessage m) {
        MongoDlqMessageEntity e = new MongoDlqMessageEntity();
        e.setId(m.getId());
        e.setMessageId(m.getMessageId());
        e.setPayload(m.getPayload());
        e.setErrorMessage(m.getErrorMessage());
        e.setPublished(m.isPublished());
        e.setCreatedAt(m.getCreatedAt());
        e.setPublishedAt(m.getPublishedAt());
        return e;
    }

    public DlqMessage toDomain(MongoDlqMessageEntity e) {
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
