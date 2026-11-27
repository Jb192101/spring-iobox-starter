package org.jedi_bachelor.ioboxstarter.db.adapter.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoOutboxMessageEntity;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.mongo.MongoOutboxRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public final class MongoOutboxAdapter implements PortOutboxRepository {

  private final MongoOutboxRepository repository;

  @Override
  public List<OutboxMessage> findUnpublishedOrderByCreatedAtAsc() {
    return repository.findUnpublishedOrderByCreatedAtAsc().stream().map(MongoOutboxMappingUtil::toDomain).toList();
  }

  @Override
  public List<OutboxMessage> findLatestUnpublishedMessages() {
    return repository
        .findLatestUnpublishedMessages(
            PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt")))
        .stream()
        .map(MongoOutboxMappingUtil::toDomain)
        .toList();
  }

  @Override
  public int deleteProcessedOlderThan(LocalDateTime olderThan) {
    return repository.deletePublishedOlderThan(olderThan);
  }

  @Override
  public int deleteFailedMessages(int maxRetries) {
    return repository.deleteFailedMessages(maxRetries);
  }

  @Override
  public Optional<OutboxMessage> findByMessageId(String messageId) {
    return repository.findByMessageId(messageId).map(MongoOutboxMappingUtil::toDomain);
  }

  @Override
  public OutboxMessage save(OutboxMessage message) {
    return MongoOutboxMappingUtil.toDomain(repository.save(MongoOutboxMappingUtil.toEntity(message)));
  }

  @Override
  public void delete(OutboxMessage message) {
    repository.deleteById(message.getId());
  }

  @Override
  public List<OutboxMessage> findAll() {
    List<MongoOutboxMessageEntity> nativeMessages = repository.findAll();

    return nativeMessages.stream().map(MongoOutboxMappingUtil::toDomain).toList();
  }
}
