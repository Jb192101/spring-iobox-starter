package org.jedi_bachelor.ioboxstarter.db.repository.cassandra;

import java.util.List;
import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraDlqMessageEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface CassandraDlqRepository extends CassandraRepository<CassandraDlqMessageEntity, String> {

    @AllowFiltering
    @Query("SELECT * FROM dlq_messages WHERE status = 'NEW' ALLOW FILTERING")
    List<CassandraDlqMessageEntity> findUnpublished();
}