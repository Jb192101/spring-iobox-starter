package org.jedi_bachelor.ioboxstarter.tests;

import com.example.testapp.events.TestEvent;
import org.jedi_bachelor.ioboxstarter.OutboxContextManager;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class InboxTest extends AbstractTest {

    @Autowired
    private OutboxContextManager outboxContextManager;

    @Autowired
    private PortInboxRepository inboxRepository;

    @Test
    public void readMessageFromTopicAndProcessInbox() {
        // Sending message to topic
        TestEvent event = new TestEvent(300, "author");
        outboxContextManager.save(event);

        // Reading messages
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            assertThat(inboxRepository.findUnprocessedMessages().isEmpty())
                    .as("All messages in Inbox repository must be published");
        });
    }
}
