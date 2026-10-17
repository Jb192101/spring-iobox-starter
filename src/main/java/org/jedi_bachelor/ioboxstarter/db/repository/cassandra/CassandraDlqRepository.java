package org.jedi_bachelor.ioboxstarter.db.repository.cassandra;

import org.jedi_bachelor.ioboxstarter.db.entity.cassandra.CassandraDlqMessageEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface CassandraDlqRepository extends CassandraRepository<CassandraDlqMessageEntity, String> {

    @AllowFiltering
    @Query("SELECT * FROM dlq_messages WHERE published = false ALLOW FILTERING")
    List<CassandraDlqMessageEntity> findUnpublished();
}