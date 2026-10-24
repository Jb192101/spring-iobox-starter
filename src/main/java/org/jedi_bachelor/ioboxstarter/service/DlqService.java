package org.jedi_bachelor.ioboxstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerContext;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DlqService {

    private final PortDlqRepository deadLettersRepository;

    private final BrokerContext brokerContext;

    @Transactional
    public void process(final DlqMessage message) {
        this.brokerContext.publishDeadLetter(message);

        message.markAsPublished();

        this.deadLettersRepository.save(message);
    }

    public List<DlqMessage> findAll() {
        return this.deadLettersRepository.findAll();
    }

    public List<DlqMessage> findUnpublishedMessages() {
        return this.deadLettersRepository.findUnpublishedMessages();
    }
}
