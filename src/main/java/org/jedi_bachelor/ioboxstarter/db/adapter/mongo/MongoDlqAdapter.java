package org.jedi_bachelor.ioboxstarter.db.adapter.mongo;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.mongo.MongoDlqRepository;

@RequiredArgsConstructor
public final class MongoDlqAdapter implements PortDlqRepository {

    private final MongoDlqRepository repository;

    @Override
    public List<DlqMessage> findUnpublishedMessages() {
        return repository.findUnpublishedMessages()
                .stream().map(MongoDlqMappingUtil::toDomain).toList();
    }

    @Override
    public DlqMessage save(DlqMessage message) {
        return MongoDlqMappingUtil.toDomain(repository.save(MongoDlqMappingUtil.toEntity(message)));
    }

    @Override
    public void delete(DlqMessage message) {
        repository.deleteById(message.getId());
    }

    @Override
    public List<DlqMessage> findAll() {
        List<DlqMessage> list = new ArrayList<>();
        repository.findAll().forEach(e -> list.add(MongoDlqMappingUtil.toDomain(e)));
        return list;
    }
}
