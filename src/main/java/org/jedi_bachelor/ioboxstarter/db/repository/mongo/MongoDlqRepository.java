package org.jedi_bachelor.ioboxstarter.db.repository.mongo;

import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoDlqMessageEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoDlqRepository
        extends CrudRepository<MongoDlqMessageEntity, String> {

    @Query("{ 'published' : false }")
    List<MongoDlqMessageEntity> findUnpublishedMessages();
}