package org.jedi_bachelor.ioboxstarter.repository;

import org.jedi_bachelor.ioboxstarter.model.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    @Query("SELECT m FROM OutboxMessage m WHERE m.published = false ORDER BY m.createdAt ASC")
    List<OutboxMessage> findUnpublishedOrderByCreatedAtAsc();

    @Query("""
        SELECT m FROM OutboxMessage m 
        WHERE m.published = false 
        AND m.createdAt = (
            SELECT MAX(m2.createdAt) 
            FROM OutboxMessage m2 
            WHERE m2.messageId = m.messageId 
            AND m2.published = false
        )
        ORDER BY m.createdAt ASC
    """)
    List<OutboxMessage> findLatestUnpublishedMessages();

    @Query("SELECT m FROM OutboxMessage m WHERE m.published = false AND m.retryCount < :maxRetries")
    List<OutboxMessage> findRetryableMessages(@Param("maxRetries") int maxRetries);

    @Modifying
    @Query("DELETE FROM OutboxMessage m WHERE m.published = true AND m.publishedAt < :olderThan")
    int deleteProcessedOlderThan(@Param("olderThan") LocalDateTime olderThan);

    @Modifying
    @Query("DELETE FROM OutboxMessage m WHERE m.published = false AND m.retryCount >= :maxRetries")
    int deleteFailedMessages(@Param("maxRetries") int maxRetries);

    Optional<OutboxMessage> findByMessageId(String messageId);
}
