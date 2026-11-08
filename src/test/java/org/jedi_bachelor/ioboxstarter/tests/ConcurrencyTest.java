package org.jedi_bachelor.ioboxstarter.tests;

import org.jedi_bachelor.ioboxstarter.OutboxContextManager;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "iobox.outbox.scheduler.concurrency-level=3",
        "iobox.inbox.scheduler.concurrency-level=3"
})
public class ConcurrencyTest extends AbstractTest {

    @Autowired
    private OutboxContextManager outboxContextManager;

    @Autowired
    private PortOutboxRepository portOutboxRepository;

    @Autowired
    private PortInboxRepository portInboxRepository;

    //@Test
    public void send50MessagesInThreeTopicsWithThreeThreadsAndRead() {

    }
}
