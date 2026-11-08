package org.jedi_bachelor.ioboxstarter.tests;

import com.example.testapp.events.OrderCreatedEvent;
import com.example.testapp.events.TestEvent;
import com.example.testapp.events.TestSuccessEvent;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jedi_bachelor.ioboxstarter.OutboxContextManager;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class OutboxTest extends AbstractTest {

    @Autowired
    private OutboxContextManager outboxContextManager;

    @Autowired
    private PortOutboxRepository outboxRepository;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    public void saveAndSendOutboxMessage() {
        TestEvent event = new TestEvent(300, "author");
        outboxContextManager.save(event);

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
            assertThat(outboxRepository.findLatestUnpublishedMessages().isEmpty())
                    .as("Message in Outbox repository must be published");
        });
    }

    @Test
    public void send50MessagesInThreeTopicsWithOneThread() {
        for(int i = 0; i < 50; i++) {
            Object event;
            if(i % 3 == 0) {
                event = new TestEvent(300, "author");
            } else if(i % 3 == 1) {
                event = new OrderCreatedEvent(Long.valueOf(i), 100.0);
            } else {
                event = new TestSuccessEvent(String.valueOf(i));
            }
            outboxContextManager.save(event);
        }

        try (KafkaConsumer<String, String> consumer = createTestConsumer()) {
            consumer.subscribe(List.of("user-created"));

            await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                assertThat(records.count())
                        .as("Messages must be in user-created topic")
                        .isGreaterThan(0);
            });
        }

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(outboxRepository.findLatestUnpublishedMessages().isEmpty())
                    .as("All messages in Outbox repository must be published");
        });
    }
}
