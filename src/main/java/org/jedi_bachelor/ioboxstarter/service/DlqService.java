package org.jedi_bachelor.ioboxstarter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.status.DeadLetterStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DlqService {

  private final PortDlqRepository deadLettersRepository;

  private final BrokerStrategy brokerStrategy;

  private final Counter dlqCounter;

  public DlqService(
      PortDlqRepository dlqRepository, BrokerStrategy brokerStrategy, MeterRegistry meterRegistry) {
    this.deadLettersRepository = dlqRepository;
    this.brokerStrategy = brokerStrategy;

    this.dlqCounter = meterRegistry.counter("spring.iobox.dlq.message.amount");
  }

  @Transactional
  public void process(final DlqMessage message) {
    if (!message.setStatus(DeadLetterStatus.PENDING)) {
      log.warn(
          "Cannot transition DLQ message {} to PENDING, current status: {}",
          message.getId(),
          message.getStatus());
      return;
    }

    brokerStrategy.publishDeadLetter(message);

    message.markAsPublished();

    deadLettersRepository.save(message);

    dlqCounter.increment();
  }

  public List<DlqMessage> findAll() {
    return deadLettersRepository.findAll();
  }

  public List<DlqMessage> findUnpublishedMessages() {
    return deadLettersRepository.findUnpublishedMessages();
  }
}
