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
        return List.of();
    }

    @Override
    public List<InboxMessage> findUnprocessedByQueue(String queueName) {
        return List.of();
    }

    @Override
    public Optional<InboxMessage> findByMessageId(String messageId) {
        return Optional.empty();
    }

    @Override
    public int deleteProcessedOlderThan(LocalDateTime olderThan) {
        return 0;
    }

    @Override
    public int deleteFailedMessages(int maxRetries) {
        return 0;
    }

    @Override
    public InboxMessage save(InboxMessage message) {
        return null;
    }

    @Override
    public void delete(InboxMessage message) {

    }

    @Override
    public List<InboxMessage> findAll() {
        return List.of();
    }
}
