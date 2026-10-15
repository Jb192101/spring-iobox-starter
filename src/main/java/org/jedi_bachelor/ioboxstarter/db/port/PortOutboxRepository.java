package org.jedi_bachelor.ioboxstarter.db.port;

import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Публичный репозиторий (используется без Generic-ов)
 */

public interface PortOutboxRepository {

    List<OutboxMessage> findUnpublishedOrderByCreatedAtAsc();

    List<OutboxMessage> findLatestUnpublishedMessages();

    List<OutboxMessage> findRetryableMessages(int maxRetries);

    int deleteProcessedOlderThan(LocalDateTime olderThan);

    int deleteFailedMessages(int maxRetries);

    Optional<OutboxMessage> findByMessageId(String messageId);

    OutboxMessage save(OutboxMessage message);

    void delete(OutboxMessage message);

    List<OutboxMessage> findAll();
}
