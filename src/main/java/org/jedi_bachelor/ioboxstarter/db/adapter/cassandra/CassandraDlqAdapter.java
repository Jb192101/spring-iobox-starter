package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraDlqRepository;

@RequiredArgsConstructor
public final class CassandraDlqAdapter implements PortDlqRepository {

    private final CassandraDlqRepository repository;

    @Override
    public List<DlqMessage> findUnpublishedMessages() {
        return repository.findUnpublished().stream()
                .map(CassandraDlqMappingUtil::toDomain).toList();
    }

    @Override
    public DlqMessage save(DlqMessage message) {
        return CassandraDlqMappingUtil.toDomain(repository.save(CassandraDlqMappingUtil.toEntity(message)));
    }

    @Override
    public void delete(DlqMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<DlqMessage> findAll() {
        return repository.findAll().stream()
                .map(CassandraDlqMappingUtil::toDomain).toList();
    }
}
