package org.jedi_bachelor.ioboxstarter.repository;

import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeadLettersRepository extends JpaRepository<DeadLettersEntity, Long> {
    @Query("SELECT m FROM DeadLettersEntity m WHERE m.published = false ORDER BY m.createdAt ASC")
    List<DeadLettersEntity> findUnpublishedMessages();
}
