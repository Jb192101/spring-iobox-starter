package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaDlqRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class JpaDlqAdapter implements PortDlqRepository {

    private final JpaDlqRepository repository;

    private final JpaDlqMapper mapper;

    @Override
    public List<DlqMessage> findUnpublishedMessages() {
        return repository.findUnpublishedMessages()
                .stream().map(mapper::toDomain).toList();
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
        List<DlqMessage> list = new ArrayList<>();
        repository.findAll().forEach(e -> list.add(mapper.toDomain(e)));
        return list;
    }
}
