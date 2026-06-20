package org.jedi_bachelor.ioboxstarter.repository;

import org.jedi_bachelor.ioboxstarter.core.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Базовый репозиторий для Outbox-сообщений.
 * Использует дженерики, чтобы подходить для любых наследников OutboxMessage.
 */

@NoRepositoryBean
public interface OutboxRepository<T extends OutboxMessage> extends JpaRepository<T, Long> {
    /**
     * Найти все неопубликованные сообщения в порядке создания
     */
    @Query("SELECT m FROM #{#entityName} m WHERE m.published = false ORDER BY m.createdAt ASC")
    List<T> findUnpublishedOrderByCreatedAtAsc();

    /**
     * Найти неопубликованные сообщения для конкретного топика
     */
    @Query("SELECT m FROM #{#entityName} m WHERE m.published = false AND m.topic = :topic ORDER BY m.createdAt ASC")
    List<T> findUnpublishedByTopic(@Param("topic") String topic);

    /**
     * Пометить сообщение как опубликованное
     */
    @Modifying
    @Query("UPDATE #{#entityName} m SET m.published = true, m.publishedAt = :publishedAt WHERE m.id = :id")
    void markAsPublished(@Param("id") Long id, @Param("publishedAt") LocalDateTime publishedAt);

    /**
     * Увеличить счётчик retry
     */
    @Modifying
    @Query("UPDATE #{#entityName} m SET m.retryCount = m.retryCount + 1 WHERE m.id = :id")
    void incrementRetryCount(@Param("id") Long id);

    /**
     * Пометить как DEAD (после исчерпания retry)
     */
    @Modifying
    @Query("UPDATE #{#entityName} m SET m.status = 'DEAD', m.errorMessage = :errorMessage WHERE m.id = :id")
    void markAsDead(@Param("id") Long id, @Param("errorMessage") String errorMessage);

    /**
     * Дедупликация: найти последнее сообщение для каждого messageId
     */
    @Query("""
        SELECT m FROM #{#entityName} m 
        WHERE m.published = false 
        AND m.createdAt = (
            SELECT MAX(m2.createdAt) 
            FROM #{#entityName} m2 
            WHERE m2.messageId = m.messageId 
            AND m2.published = false
        )
        ORDER BY m.createdAt ASC
    """)
    List<T> findLatestUnpublishedMessages();

    /**
     * Удалить старые обработанные сообщения (для очистки)
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} m WHERE m.published = true AND m.processedAt < :olderThan")
    void deleteProcessedOlderThan(@Param("olderThan") LocalDateTime olderThan);
}
