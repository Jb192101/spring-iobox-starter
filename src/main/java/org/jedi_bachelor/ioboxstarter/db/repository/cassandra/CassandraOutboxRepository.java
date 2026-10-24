package org.jedi_bachelor.ioboxstarter.db.repository.cassandra;

import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraOutboxMessageEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CassandraOutboxRepository extends CassandraRepository<CassandraOutboxMessageEntity, String> {

    @AllowFiltering
    @Query("SELECT * FROM outbox_messages WHERE published = false ALLOW FILTERING")
    List<CassandraOutboxMessageEntity> findUnpublished();

    @AllowFiltering
    @Query("SELECT * FROM outbox_messages WHERE published = false AND retryCount < ?0 ALLOW FILTERING")
    List<CassandraOutboxMessageEntity> findRetryable(int maxRetries);

    @AllowFiltering
    Optional<CassandraOutboxMessageEntity> findByMessageId(String messageId);
}