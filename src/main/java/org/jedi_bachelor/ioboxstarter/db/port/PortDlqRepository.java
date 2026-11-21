package org.jedi_bachelor.ioboxstarter.db.port;

import java.util.List;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;

public interface PortDlqRepository {

  List<DlqMessage> findUnpublishedMessages();

  DlqMessage save(DlqMessage message);

  void delete(DlqMessage message);

  List<DlqMessage> findAll();
}
