package org.jedi_bachelor.ioboxstarter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
public class DlqService {

    private final PortDlqRepository deadLettersRepository;

    private final BrokerContext brokerContext;

    private Counter dlqCounter;

    public DlqService(PortDlqRepository dlqRepository, BrokerContext brokerContext, MeterRegistry meterRegistry) {
        this.deadLettersRepository = dlqRepository;
        this.brokerContext = brokerContext;

        this.dlqCounter = meterRegistry.counter("spring.iobox.dlq.message.amount");
    }

    @Transactional
    public void process(final DlqMessage message) {
        this.brokerContext.publishDeadLetter(message);

        message.markAsPublished();

        this.deadLettersRepository.save(message);

        dlqCounter.increment();
    }

    public List<DlqMessage> findAll() {
        return this.deadLettersRepository.findAll();
    }

    public List<DlqMessage> findUnpublishedMessages() {
        return this.deadLettersRepository.findUnpublishedMessages();
    }
}
