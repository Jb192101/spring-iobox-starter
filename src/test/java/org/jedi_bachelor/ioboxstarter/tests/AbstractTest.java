package org.jedi_bachelor.ioboxstarter.tests;

import com.example.testapp.TestIoboxApplication;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes = TestIoboxApplication.class)
@Testcontainers
@TestPropertySource(properties = {
        "iobox.outbox.scheduler.interval=500",
        "iobox.inbox.scheduler.interval=500",
        "iobox.storage=jpa",
        "iobox.broker=kafka"
})
abstract public class AbstractTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:16-alpine");

    protected KafkaConsumer<String, String> createTestConsumer() {
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
