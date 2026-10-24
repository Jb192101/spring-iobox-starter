package org.jedi_bachelor.ioboxstarter.db.adapter.mongo;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.mongo.MongoInboxRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public final class MongoInboxAdapter implements PortInboxRepository {

    private final MongoInboxRepository repository;

    private final MongoInboxMapper mapper;

    @Override
    public List<InboxMessage> findUnprocessedMessages() {
        return repository.findUnprocessedMessages()
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<InboxMessage> findUnprocessedByQueue(String queueName) {
        return repository.findUnprocessedByQueue(queueName)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<InboxMessage> findByMessageId(String messageId) {
        return repository.findByMessageId(messageId).map(mapper::toDomain);
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
        return mapper.toDomain(repository.save(mapper.toEntity(message)));
    }

    @Override
    public void delete(InboxMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<InboxMessage> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}