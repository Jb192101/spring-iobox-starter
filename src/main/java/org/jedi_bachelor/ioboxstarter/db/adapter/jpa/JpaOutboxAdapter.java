package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaOutboxRepository;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaOutboxAdapter implements PortOutboxRepository {

    private final JpaOutboxRepository repository;

    private final JpaOutboxMapper mapper;

    @Override
    public List<OutboxMessage> findUnpublishedOrderByCreatedAtAsc() {
        return repository.findUnpublishedOrderByCreatedAtAsc()
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OutboxMessage> findLatestUnpublishedMessages() {
        return repository.findLatestUnpublishedMessages(PageRequest.of(0, 100))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OutboxMessage> findRetryableMessages(int maxRetries) {
        return repository.findRetryableMessages(maxRetries)
                .stream().map(mapper::toDomain).toList();
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
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}