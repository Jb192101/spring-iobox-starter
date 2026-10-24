package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraInboxRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CassandraInboxAdapter implements PortInboxRepository {

    private final CassandraInboxRepository repository;

    private final CassandraInboxMapper mapper;

    @Override
    public List<InboxMessage> findUnprocessedMessages() {
        return repository.findUnprocessed().stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<InboxMessage> findUnprocessedByQueue(String queueName) {
        return repository.findUnprocessedByQueue(queueName).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public Optional<InboxMessage> findByMessageId(String messageId) {
        return repository.findByMessageId(messageId).map(mapper::toDomain);
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
        return mapper.toDomain(repository.save(mapper.toEntity(message)));
    }

    @Override
    public void delete(InboxMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<InboxMessage> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain).toList();
    }
}