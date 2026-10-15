package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.jpa.JpaDlqMessageEntity;

public final class JpaDlqMapper {

    public JpaDlqMessageEntity toEntity(DlqMessage m) {
        JpaDlqMessageEntity e = new JpaDlqMessageEntity();
        e.setId(m.getId());
        e.setMessageId(m.getMessageId());
        e.setPayload(m.getPayload());
        e.setErrorMessage(m.getErrorMessage());
        e.setPublished(m.isPublished());
        e.setCreatedAt(m.getCreatedAt());
        e.setPublishedAt(m.getPublishedAt());
        return e;
    }

    public DlqMessage toDomain(JpaDlqMessageEntity e) {
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
