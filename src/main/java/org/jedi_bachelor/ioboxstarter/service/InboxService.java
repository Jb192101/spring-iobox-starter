package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.model.InboxMessage;
import org.jedi_bachelor.ioboxstarter.repository.InboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InboxService {
    private final InboxRepository repository;

    @Transactional
    public List<InboxMessage> getAllMessages() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<InboxMessage> getMessageById(Long id) {
        return this.repository.findById(id);
    }
}
