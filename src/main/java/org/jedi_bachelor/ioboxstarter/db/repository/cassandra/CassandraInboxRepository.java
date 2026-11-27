package org.jedi_bachelor.ioboxstarter.db.repository.cassandra;

import java.util.List;
import java.util.Optional;
import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraInboxMessageEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface CassandraInboxRepository
    extends CassandraRepository<CassandraInboxMessageEntity, String> {

  @AllowFiltering
  @Query("SELECT * FROM inbox_messages WHERE status = 'NEW' OR status = 'FAILED' ALLOW FILTERING")
  List<CassandraInboxMessageEntity> findUnprocessed();

  @AllowFiltering
  @Query("SELECT * FROM inbox_messages WHERE messageId = ?0 ALLOW FILTERING")
  Optional<CassandraInboxMessageEntity> findByMessageId(String messageId);
}
