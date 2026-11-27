package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaInboxRepository;

@RequiredArgsConstructor
public final class JpaInboxAdapter implements PortInboxRepository {

  private final JpaInboxRepository repository;

  @Override
  public List<InboxMessage> findUnprocessedMessages() {
    return repository.findUnprocessedMessages().stream().map(JpaInboxMappingUtil::toDomain).toList();
  }

  @Override
  public Optional<InboxMessage> findByMessageId(String messageId) {
    return repository.findByMessageId(messageId).map(JpaInboxMappingUtil::toDomain);
  }

  @Override
  public int deleteProcessedOlderThan(LocalDateTime olderThan) {
    return repository.deleteProcessedOlderThan(olderThan);
  }

  @Override
  public int deleteFailedMessages(int maxRetries) {
    return repository.deleteFailedMessages(maxRetries);
  }

  @Override
  public InboxMessage save(InboxMessage message) {
    return JpaInboxMappingUtil.toDomain(repository.save(JpaInboxMappingUtil.toEntity(message)));
  }

  @Override
  public void delete(InboxMessage message) {
    repository.deleteById(message.getId());
  }

  @Override
  public List<InboxMessage> findAll() {
    return repository.findAll().stream().map(JpaInboxMappingUtil::toDomain).toList();
  }
}
