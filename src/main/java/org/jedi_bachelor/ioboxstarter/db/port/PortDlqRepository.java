package org.jedi_bachelor.ioboxstarter.db.port;

import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;

import java.util.List;

public interface PortDlqRepository {

    List<DlqMessage> findUnpublishedMessages();

    DlqMessage save(DlqMessage message);

    void delete(DlqMessage message);

    List<DlqMessage> findAll();
}
