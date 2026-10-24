package org.jedi_bachelor.ioboxstarter.db.repository.cassandra;

import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraInboxMessageEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CassandraInboxRepository extends CassandraRepository<CassandraInboxMessageEntity, String> {

    @Query("SELECT * FROM inbox_messages WHERE processed = false ALLOW FILTERING")
    List<CassandraInboxMessageEntity> findUnprocessed();

    @Query("SELECT * FROM inbox_messages WHERE processed = false AND queueName = ?0 ALLOW FILTERING")
    List<CassandraInboxMessageEntity> findUnprocessedByQueue(String queueName);

    @Query("SELECT * FROM inbox_messages WHERE messageId = ?0 ALLOW FILTERING")
    Optional<CassandraInboxMessageEntity> findByMessageId(String messageId);
}