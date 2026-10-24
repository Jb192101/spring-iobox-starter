package org.jedi_bachelor.ioboxstarter.db.repository.mongo;

import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoOutboxMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MongoOutboxRepository
        extends CrudRepository<MongoOutboxMessageEntity, String> {

    @Query("{ 'published' : false }")
    List<MongoOutboxMessageEntity> findUnpublishedOrderByCreatedAtAsc();

    @Query("{ 'published' : false }")
    List<MongoOutboxMessageEntity> findLatestUnpublishedMessages(Pageable pageable);

    @Query("{ 'published' : false, 'retryCount' : { $lt : ?0 } }")
    List<MongoOutboxMessageEntity> findRetryableMessages(int maxRetries);

    @Query(value = "{ 'published' : true, 'publishedAt' : { $lt : ?0 } }", delete = true)
    int deletePublishedOlderThan(LocalDateTime olderThan);

    @Query(value = "{ 'published' : false, 'retryCount' : { $gte : ?0 } }", delete = true)
    int deleteFailedMessages(int maxRetries);

    Optional<MongoOutboxMessageEntity> findByMessageId(String messageId);

    @Override
    List<MongoOutboxMessageEntity> findAll();
}