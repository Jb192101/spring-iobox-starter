package org.jedi_bachelor.ioboxstarter.db.repository.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoInboxMessageEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoInboxRepository extends CrudRepository<MongoInboxMessageEntity, String> {

    @Query("{ 'status' : { $in : ['NEW', 'FAILED'] } }")
    List<MongoInboxMessageEntity> findUnprocessedMessages();

    @Query("{ 'status' : { $in : ['NEW', 'FAILED'] }, 'queueName' : ?0 }")
    List<MongoInboxMessageEntity> findUnprocessedByQueue(String queueName);

    Optional<MongoInboxMessageEntity> findByMessageId(String messageId);

    @Query(value = "{ 'status' : 'PROCESSED', 'processedAt' : { $lt : ?0 } }", delete = true)
    int deleteProcessedOlderThan(LocalDateTime olderThan);

    @Query(value = "{ 'status' : 'COMPLETELY_FAILED', 'retryCount' : { $gte : ?0 } }", delete = true)
    int deleteFailedMessages(int maxRetries);

    @NotNull
    List<MongoInboxMessageEntity> findAll();
}