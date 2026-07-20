package org.jedi_bachelor.ioboxstarter.repository;

import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InboxRepository extends JpaRepository<InboxMessage, Long> {
    @Query("SELECT m FROM InboxMessage m WHERE m.processed = false ORDER BY m.createdAt ASC")
    List<InboxMessage> findUnprocessedMessages();

    @Query("SELECT m FROM InboxMessage m WHERE m.processed = false AND m.queueName = :queueName ORDER BY m.createdAt ASC")
    List<InboxMessage> findUnprocessedByQueue(@Param("queueName") String queueName);

    Optional<InboxMessage> findByMessageId(String messageId);

    @Modifying
    @Query("DELETE FROM InboxMessage m WHERE m.processed = true AND m.processedAt < :olderThan")
    int deleteProcessedOlderThan(@Param("olderThan") LocalDateTime olderThan);

    @Modifying
    @Query("DELETE FROM InboxMessage m WHERE m.processed = false AND m.retryCount >= :maxRetries")
    int deleteFailedMessages(@Param("maxRetries") int maxRetries);
}