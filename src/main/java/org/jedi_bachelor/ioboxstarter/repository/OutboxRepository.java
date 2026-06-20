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
