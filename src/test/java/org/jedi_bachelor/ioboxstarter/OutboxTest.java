package org.jedi_bachelor.ioboxstarter;

import com.example.testapp.events.TestEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                        .as("Сообщение должно появиться в топике user-created")
                        .isGreaterThan(0);
            });
        }

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(outboxRepository.findLatestUnpublishedMessages().isEmpty())
                    .as("Все сообщения в Outbox должны быть опубликованы");
        });
    }

    @Test
    public void send50MessagesInThreeTopicsWithOneThread() {

    }

    private KafkaConsumer<String, String> createTestConsumer() {
        Map<String, Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );

        return new KafkaConsumer<>(props);
    }
}
