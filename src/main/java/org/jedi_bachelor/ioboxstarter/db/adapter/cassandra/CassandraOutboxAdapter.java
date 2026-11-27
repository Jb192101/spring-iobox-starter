package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraOutboxMessageEntity;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraOutboxRepository;

@RequiredArgsConstructor
public final class CassandraOutboxAdapter implements PortOutboxRepository {

    private final CassandraOutboxRepository repository;

    @Override
    public List<OutboxMessage> findUnpublishedOrderByCreatedAtAsc() {
        return repository.findUnpublished().stream()
                .sorted(Comparator.comparing(CassandraOutboxMessageEntity::getCreatedAt))
                .map(CassandraOutboxMappingUtil::toDomain).toList();
    }

    @Override
    public List<OutboxMessage> findLatestUnpublishedMessages() {
        return repository.findUnpublished().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(100)
                .map(CassandraOutboxMappingUtil::toDomain).toList();
    }

    @Override
    public int deleteProcessedOlderThan(LocalDateTime olderThan) {
        throw new UnsupportedOperationException("Realisation based on TTL");
    }

    @Override
    public int deleteFailedMessages(int maxRetries) {
        throw new UnsupportedOperationException("Realisation based on TTL");
    }

    @Override
    public Optional<OutboxMessage> findByMessageId(String messageId) {
        return repository.findByMessageId(messageId).map(CassandraOutboxMappingUtil::toDomain);
    }

    @Override
    public OutboxMessage save(OutboxMessage message) {
        return CassandraOutboxMappingUtil.toDomain(repository.save(CassandraOutboxMappingUtil.toEntity(message)));
    }

    @Override
    public void delete(OutboxMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<OutboxMessage> findAll() {
        return repository.findAll().stream()
                .map(CassandraOutboxMappingUtil::toDomain).toList();
    }
}
