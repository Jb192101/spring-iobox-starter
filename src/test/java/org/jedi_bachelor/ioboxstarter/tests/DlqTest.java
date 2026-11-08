package org.jedi_bachelor.ioboxstarter.tests;

import com.example.testapp.events.InvalidTestEvent;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jedi_bachelor.ioboxstarter.OutboxContextManager;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@TestPropertySource(properties = {
        "iobox.dlq.enabled=true"
})
public class DlqTest extends AbstractTest {

    @Autowired
    private OutboxContextManager outboxContextManager;

    @Autowired
    private PortDlqRepository portDlqRepository;

    @Autowired
    private PortInboxRepository portInboxRepository;

    @Test
    public void invalidMessageSavedInDlqTable() {
        InvalidTestEvent event = new InvalidTestEvent(300);
        outboxContextManager.save(event, "user-created");

        try (KafkaConsumer<String, String> consumer = createTestConsumer()) {
            consumer.subscribe(List.of("user-created"));

            await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                assertThat(records.count())
                        .as("Message must be in user-created topic")
                        .isGreaterThan(0);
            });
        }

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(!portDlqRepository.findAll().isEmpty())
                    .as("In DLQ repository must be at least one message");
        });
    }

    //@Test
    public void invalidMessageSendedToDlqTopic() {
        InvalidTestEvent event = new InvalidTestEvent(300);
        outboxContextManager.save(event, "user-created");

        KafkaConsumer<String, String> userCreatedConsumer = createTestConsumer();
        userCreatedConsumer.subscribe(List.of("user-created"));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            ConsumerRecords<String, String> records = userCreatedConsumer.poll(Duration.ofMillis(500));
            assertThat(records.count())
                    .as("Message must be in user-created topic")
                    .isGreaterThan(0);
        });

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(!portDlqRepository.findAll().isEmpty())
                    .as("In DLQ repository must be at least one message");
        });

        KafkaConsumer<String, String> deadLettersConsumer = createTestConsumer();
        deadLettersConsumer.subscribe(List.of("dead-letters"));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            ConsumerRecords<String, String> records = deadLettersConsumer.poll(Duration.ofMillis(500));
            assertThat(records.count())
                    .as("Message must be in dead-letters topic")
                    .isGreaterThan(0);
        });
    }
}
