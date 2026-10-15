package org.jedi_bachelor.ioboxstarter.db.repository.jpa;

import org.jedi_bachelor.ioboxstarter.db.entity.jpa.JpaDlqMessageEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaDlqRepository
        extends CrudRepository<JpaDlqMessageEntity, String> {

    @Query("SELECT d FROM JpaDlqMessageEntity d WHERE d.published = false ORDER BY d.createdAt ASC")
    List<JpaDlqMessageEntity> findUnpublishedMessages();
}
