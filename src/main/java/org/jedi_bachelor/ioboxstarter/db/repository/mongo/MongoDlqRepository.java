package org.jedi_bachelor.ioboxstarter.db.repository.mongo;

import java.util.List;
import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoDlqMessageEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoDlqRepository extends CrudRepository<MongoDlqMessageEntity, String> {

    @Query("{ 'status' : 'NEW' }")
    List<MongoDlqMessageEntity> findUnpublishedMessages();
}