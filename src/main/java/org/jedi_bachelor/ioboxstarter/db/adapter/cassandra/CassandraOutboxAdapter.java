package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraOutboxRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CassandraOutboxAdapter implements PortOutboxRepository {

    private final CassandraOutboxRepository repository;

    private final CassandraOutboxMapper mapper;

    @Override
    public List<OutboxMessage> findUnpublishedOrderByCreatedAtAsc() {
        return repository.findUnpublished().stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<OutboxMessage> findLatestUnpublishedMessages() {
        return repository.findUnpublished().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(100)
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<OutboxMessage> findRetryableMessages(int maxRetries) {
        return repository.findRetryable(maxRetries).stream()
                .map(mapper::toDomain).toList();
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
        return repository.findByMessageId(messageId).map(mapper::toDomain);
    }

    @Override
    public OutboxMessage save(OutboxMessage message) {
        return mapper.toDomain(repository.save(mapper.toEntity(message)));
    }

    @Override
    public void delete(OutboxMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<OutboxMessage> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain).toList();
    }
}
