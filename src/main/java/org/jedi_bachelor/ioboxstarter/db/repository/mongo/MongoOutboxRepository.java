package org.jedi_bachelor.ioboxstarter.db.repository.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jedi_bachelor.ioboxstarter.db.entity.mongo.MongoOutboxMessageEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoOutboxRepository extends CrudRepository<MongoOutboxMessageEntity, String> {

  @Query("{ 'status' : { $in : ['NEW', 'FAILED'] } }")
  List<MongoOutboxMessageEntity> findUnpublishedOrderByCreatedAtAsc();

  @Query("{ 'status' : { $in : ['NEW', 'FAILED'] } }")
  List<MongoOutboxMessageEntity> findLatestUnpublishedMessages(Pageable pageable);

  @Query(value = "{ 'status' : 'PROCESSED', 'publishedAt' : { $lt : ?0 } }", delete = true)
  int deletePublishedOlderThan(LocalDateTime olderThan);

  @Query(value = "{ 'status' : 'COMPLETELY_FAILED', 'retryCount' : { $gte : ?0 } }", delete = true)
  int deleteFailedMessages(int maxRetries);

  Optional<MongoOutboxMessageEntity> findByMessageId(String messageId);

  @NotNull
  @Override
  List<MongoOutboxMessageEntity> findAll();
}
