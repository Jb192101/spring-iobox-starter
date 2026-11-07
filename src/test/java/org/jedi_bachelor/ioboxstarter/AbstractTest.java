package org.jedi_bachelor.ioboxstarter;

import com.example.testapp.TestIoboxApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
}
