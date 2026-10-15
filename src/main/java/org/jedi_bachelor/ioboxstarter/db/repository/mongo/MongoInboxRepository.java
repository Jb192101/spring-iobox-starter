package org.jedi_bachelor.ioboxstarter.db.repository.mongo;

import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoInboxMessageEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MongoInboxRepository
        extends CrudRepository<MongoInboxMessageEntity, String> {

    @Query("{ 'processed' : false }")
    List<MongoInboxMessageEntity> findUnprocessedMessages();

    @Query("{ 'processed' : false, 'queueName' : ?0 }")
    List<MongoInboxMessageEntity> findUnprocessedByQueue(String queueName);

    Optional<MongoInboxMessageEntity> findByMessageId(String messageId);

    @Query(value = "{ 'processed' : true, 'processedAt' : { $lt : ?0 } }", delete = true)
    int deleteProcessedOlderThan(LocalDateTime olderThan);

    @Query(value = "{ 'processed' : false, 'retryCount' : { $gte : ?0 } }", delete = true)
    int deleteFailedMessages(int maxRetries);

    List<MongoInboxMessageEntity> findAll();
}