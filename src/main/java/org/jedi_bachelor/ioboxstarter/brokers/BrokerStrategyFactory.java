package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.ioboxstarter.properties.Brokers;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
@RequiredArgsConstructor
public class BrokerStrategyFactory {
    private final DlqProperties dlqProperties;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final JmsTemplate jmsTemplate;

    private final RabbitTemplate rabbitTemplate;

    public BrokerStrategy getStrategy(Brokers brokerType) {
        return switch (brokerType) {
            case KAFKA -> this.getKafkaStrategy();
            case RABBIT -> this.getRabbitMQStrategy();
            case ACTIVEMQ -> this.getActiveMQStrategy();
        };
    }

    private RabbitMQBrokerStrategy getRabbitMQStrategy() {
        return RabbitMQBrokerStrategy.builder()
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

    private ActiveMQBrokerStrategy getActiveMQStrategy() {
        return ActiveMQBrokerStrategy.builder()
                .dlqProperties(this.dlqProperties)
                .jmsTemplate(this.jmsTemplate)
                .build();
    }
}
