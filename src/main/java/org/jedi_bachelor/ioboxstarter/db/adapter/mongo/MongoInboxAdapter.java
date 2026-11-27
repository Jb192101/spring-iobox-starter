package org.jedi_bachelor.ioboxstarter.db.adapter.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.mongo.MongoInboxRepository;

@RequiredArgsConstructor
public final class MongoInboxAdapter implements PortInboxRepository {

    private final MongoInboxRepository repository;

    @Override
    public List<InboxMessage> findUnprocessedMessages() {
        return repository.findUnprocessedMessages()
                .stream().map(MongoInboxMappingUtil::toDomain).toList();
    }

    @Override
    public Optional<InboxMessage> findByMessageId(String messageId) {
        return repository.findByMessageId(messageId).map(MongoInboxMappingUtil::toDomain);
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
        return MongoInboxMappingUtil.toDomain(repository.save(MongoInboxMappingUtil.toEntity(message)));
    }

    @Override
    public void delete(InboxMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<InboxMessage> findAll() {
        return repository.findAll().stream().map(MongoInboxMappingUtil::toDomain).toList();
    }
}