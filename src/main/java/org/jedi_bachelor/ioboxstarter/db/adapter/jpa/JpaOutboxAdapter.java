package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaOutboxRepository;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public final class JpaOutboxAdapter implements PortOutboxRepository {

    private final JpaOutboxRepository repository;

    @Override
    public List<OutboxMessage> findUnpublishedOrderByCreatedAtAsc() {
        return repository.findUnpublishedOrderByCreatedAtAsc()
                .stream().map(JpaOutboxMappingUtil::toDomain).toList();
    }

    @Override
    public List<OutboxMessage> findLatestUnpublishedMessages() {
        return repository.findLatestUnpublishedMessages(PageRequest.of(0, 100))
                .stream().map(JpaOutboxMappingUtil::toDomain).toList();
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
        return repository.findByMessageId(messageId).map(JpaOutboxMappingUtil::toDomain);
    }

    @Override
    public OutboxMessage save(OutboxMessage message) {
        return JpaOutboxMappingUtil.toDomain(repository.save(JpaOutboxMappingUtil.toEntity(message)));
    }

    @Override
    public void delete(OutboxMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<OutboxMessage> findAll() {
        return repository.findAll().stream().map(JpaOutboxMappingUtil::toDomain).toList();
    }
}