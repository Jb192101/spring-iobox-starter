package org.jedi_bachelor.ioboxstarter.db.repository.jpa;

import jakarta.transaction.Transactional;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.entity.jpa.JpaInboxMessageEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaInboxRepository
        extends CrudRepository<JpaInboxMessageEntity, String> {

    @Query("SELECT i FROM JpaInboxMessageEntity i WHERE i.processed = false ORDER BY i.createdAt ASC")
    List<JpaInboxMessageEntity> findUnprocessedMessages();

    @Query("SELECT i FROM JpaInboxMessageEntity i WHERE i.processed = false AND i.queueName = :queueName ORDER BY i.createdAt ASC")
    List<JpaInboxMessageEntity> findUnprocessedByQueue(@Param("queueName") String queueName);

    Optional<JpaInboxMessageEntity> findByMessageId(String messageId);

    @Modifying
    @Transactional
    @Query("DELETE FROM JpaInboxMessageEntity i WHERE i.processed = true AND i.processedAt < :olderThan")
    int deleteProcessedOlderThan(@Param("olderThan") LocalDateTime olderThan);

    @Modifying
    @Transactional
    @Query("DELETE FROM JpaInboxMessageEntity i WHERE i.processed = true AND i.retryCount >= :maxRetries")
    int deleteFailedMessages(@Param("maxRetries") int maxRetries);

    @Override
    List<JpaInboxMessageEntity> findAll();
}
