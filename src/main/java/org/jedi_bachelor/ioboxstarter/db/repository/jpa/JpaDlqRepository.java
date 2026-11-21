package org.jedi_bachelor.ioboxstarter.db.repository.jpa;

import java.util.List;
import org.jedi_bachelor.ioboxstarter.db.entity.jpa.JpaDlqMessageEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JpaDlqRepository extends CrudRepository<JpaDlqMessageEntity, String> {

    @Query("SELECT d FROM JpaDlqMessageEntity d WHERE d.status = 'NEW' ORDER BY d.createdAt ASC")
    List<JpaDlqMessageEntity> findUnpublishedMessages();
}