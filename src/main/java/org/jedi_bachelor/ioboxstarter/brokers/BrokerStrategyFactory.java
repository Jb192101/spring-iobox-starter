package org.jedi_bachelor.ioboxstarter.brokers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.properties.Brokers;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
@RequiredArgsConstructor
public class BrokerStrategyFactory {
    private final DlqProperties dlqProperties;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final RabbitTemplate rabbitTemplate;

    public BrokerStrategy getStrategy(Brokers brokerType) {
        return switch (brokerType) {
            case KAFKA -> this.getKafkaStrategy();
            case RABBIT -> this.getRabbitStrategy();
        };
    }

    private RabbitBrokerStrategy getRabbitStrategy() {
        return RabbitBrokerStrategy.builder()
                .dlqProperties(this.dlqProperties)
                .rabbitTemplate(this.rabbitTemplate)
                .build();
    }

    private KafkaBrokerStrategy getKafkaStrategy() {
        return KafkaBrokerStrategy.builder()
                .kafkaTemplate(this.kafkaTemplate)
                .dlqProperties(this.dlqProperties)
                .build();
    }
}
