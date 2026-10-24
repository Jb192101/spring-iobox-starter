package org.jedi_bachelor.ioboxstarter.db.adapter.cassandra;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.cassandra.CassandraDlqRepository;

import java.util.List;

@RequiredArgsConstructor
public final class CassandraDlqAdapter implements PortDlqRepository {

    private final CassandraDlqRepository repository;

    private final CassandraDlqMapper mapper;

    @Override
    public List<DlqMessage> findUnpublishedMessages() {
        return repository.findUnpublished().stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public DlqMessage save(DlqMessage message) {
        return mapper.toDomain(repository.save(mapper.toEntity(message)));
    }

    @Override
    public void delete(DlqMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<DlqMessage> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain).toList();
    }
}
