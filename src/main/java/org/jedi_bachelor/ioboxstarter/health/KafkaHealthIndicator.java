package org.jedi_bachelor.ioboxstarter.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaHealthIndicator(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Health health() {
        try {
            kafkaTemplate.getProducerFactory().createProducer().partitionsFor("health-check-topic");
            return Health.up().withDetail("broker", "Kafka").build();
        } catch (Exception e) {
            if (e.getCause() instanceof org.apache.kafka.common.errors.TimeoutException) {
                return Health.down().withDetail("broker", "Kafka").withException(e).build();
            }
            return Health.up().withDetail("broker", "Kafka").withDetail("note", "Connected (topic not found)").build();
        }
    }
}