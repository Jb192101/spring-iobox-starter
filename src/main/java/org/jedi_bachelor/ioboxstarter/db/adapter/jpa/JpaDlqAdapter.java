package org.jedi_bachelor.ioboxstarter.db.adapter.jpa;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaDlqRepository;

@RequiredArgsConstructor
public final class JpaDlqAdapter implements PortDlqRepository {

    private final JpaDlqRepository repository;

    @Override
    public List<DlqMessage> findUnpublishedMessages() {
        return repository.findUnpublishedMessages()
                .stream().map(JpaDlqMappingUtil::toDomain).toList();
    }

    @Override
    public DlqMessage save(DlqMessage message) {
        return JpaDlqMappingUtil.toDomain(repository.save(JpaDlqMappingUtil.toEntity(message)));
    }

    @Override
    public void delete(DlqMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<DlqMessage> findAll() {
        List<DlqMessage> list = new ArrayList<>();
        repository.findAll().forEach(e -> list.add(JpaDlqMappingUtil.toDomain(e)));
        return list;
    }
}
