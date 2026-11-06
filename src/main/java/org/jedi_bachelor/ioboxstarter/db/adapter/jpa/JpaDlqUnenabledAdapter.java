package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;

import java.util.List;

@Slf4j
public final class JpaDlqUnenabledAdapter implements PortDlqRepository {

    @Override
    public List<DlqMessage> findUnpublishedMessages() {
        throw new UnsupportedOperationException("If iobox.dlq.enabled property is false, this operation don't supported");
    }

    @Override
    public DlqMessage save(DlqMessage message) {
        log.info("Message {} has deleted", message);
        return null;
    }

    @Override
    public void delete(DlqMessage message) {
        throw new UnsupportedOperationException("If iobox.dlq.enabled property is false, this operation don't supported");
    }

    @Override
    public List<DlqMessage> findAll() {
        throw new UnsupportedOperationException("If iobox.dlq.enabled property is false, this operation don't supported");
    }
}
