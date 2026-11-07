package org.jedi_bachelor.ioboxstarter;

import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InboxTest extends AbstractTest {

    @Autowired
    private OutboxContextManager outboxContextManager;

    @Autowired
    private PortOutboxRepository outboxRepository;

    @Test
    public void readMessageFromTopicAndProcessInbox() {

    }
}
