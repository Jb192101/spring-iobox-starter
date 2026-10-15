package org.jedi_bachelor.ioboxstarter.db.repository.jpa;

import jakarta.transaction.Transactional;
import org.jedi_bachelor.ioboxstarter.db.entity.jpa.JpaOutboxMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaOutboxRepository
        extends CrudRepository<JpaOutboxMessageEntity, String> {

    @Query("SELECT o FROM JpaOutboxMessageEntity o WHERE o.published = false ORDER BY o.createdAt ASC")
    List<JpaOutboxMessageEntity> findUnpublishedOrderByCreatedAtAsc();

    @Query("SELECT o FROM JpaOutboxMessageEntity o WHERE o.published = false ORDER BY o.createdAt DESC")
    List<JpaOutboxMessageEntity> findLatestUnpublishedMessages(Pageable pageable);

    @Query("SELECT o FROM JpaOutboxMessageEntity o WHERE o.published = false AND o.retryCount < :maxRetries")
    List<JpaOutboxMessageEntity> findRetryableMessages(@Param("maxRetries") int maxRetries);

    @Modifying
    @Transactional
    @Query("DELETE FROM JpaOutboxMessageEntity o WHERE o.published = true AND o.publishedAt < :olderThan")
    int deletePublishedOlderThan(@Param("olderThan") LocalDateTime olderThan);

    @Modifying
    @Transactional
    @Query("DELETE FROM JpaOutboxMessageEntity o WHERE o.published = true AND o.retryCount >= :maxRetries")
    int deleteFailedMessages(@Param("maxRetries") int maxRetries);

    Optional<JpaOutboxMessageEntity> findByMessageId(String messageId);

    @Override
    List<JpaOutboxMessageEntity> findAll();
}
