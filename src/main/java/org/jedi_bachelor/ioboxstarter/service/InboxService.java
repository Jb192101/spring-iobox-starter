package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.db.domain.InboxMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InboxService {

    private final PortInboxRepository repository;

    @Transactional
    public List<InboxMessage> getAllMessages() {
        return this.repository.findAll();
    }
}
