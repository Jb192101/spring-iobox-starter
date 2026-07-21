package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.model.dlq.DeadLettersEntity;
import org.jedi_bachelor.ioboxstarter.publisher.OutboxMessagePublisher;
import org.jedi_bachelor.ioboxstarter.repository.DeadLettersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DlqService {
    private final DeadLettersRepository deadLettersRepository;

    private final OutboxMessagePublisher outboxMessagePublisher;

    @Transactional
    public void process(DeadLettersEntity message) {
        this.outboxMessagePublisher.publishDeadLetter(message);

        message.markAsPublished();

        this.deadLettersRepository.save(message);
    }

    public List<DeadLettersEntity> findAll() {
        return this.deadLettersRepository.findAll();
    }

    public List<DeadLettersEntity> findUnpublishedMessages() {
        return this.deadLettersRepository.findUnpublishedMessages();
    }
}
