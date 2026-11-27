package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraInboxRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public final class CassandraInboxAdapter implements PortInboxRepository {

  private final CassandraInboxRepository repository;

  @Override
  public List<InboxMessage> findUnprocessedMessages() {
    return repository.findUnprocessed().stream().map(CassandraInboxMappingUtil::toDomain).toList();
  }

  @Override
  public Optional<InboxMessage> findByMessageId(String messageId) {
    return repository.findByMessageId(messageId).map(CassandraInboxMappingUtil::toDomain);
  }

  @Override
  public int deleteProcessedOlderThan(LocalDateTime olderThan) {
    // Удаление через TTL
    throw new UnsupportedOperationException("Realisation based on TTL");
  }

  @Override
  public int deleteFailedMessages(int maxRetries) {
    // Удаление через TTL.
    throw new UnsupportedOperationException("Realisation based on TTL");
  }

  @Override
  public InboxMessage save(InboxMessage message) {
    return CassandraInboxMappingUtil.toDomain(repository.save(CassandraInboxMappingUtil.toEntity(message)));
  }

  @Override
  public void delete(InboxMessage message) {
    repository.deleteById(message.getId());
  }

  @Override
  public List<InboxMessage> findAll() {
    return repository.findAll().stream().map(CassandraInboxMappingUtil::toDomain).toList();
  }
}
