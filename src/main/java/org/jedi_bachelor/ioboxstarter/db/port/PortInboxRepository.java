package org.jedi_bachelor.ioboxstarter.db.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;

/** Публичный репозиторий (используется без Generic-ов) */
public interface PortInboxRepository {

  List<InboxMessage> findUnprocessedMessages();

  @SuppressWarnings("UnusedDeclaration")
  List<InboxMessage> findUnprocessedByQueue(String queueName);

  Optional<InboxMessage> findByMessageId(String messageId);

  int deleteProcessedOlderThan(LocalDateTime olderThan);

  int deleteFailedMessages(int maxRetries);

  InboxMessage save(InboxMessage message);

  void delete(InboxMessage message);

  List<InboxMessage> findAll();
}
