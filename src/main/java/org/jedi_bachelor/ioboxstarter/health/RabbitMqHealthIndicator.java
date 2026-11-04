package org.jedi_bachelor.ioboxstarter.health;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class RabbitMqHealthIndicator implements HealthIndicator {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqHealthIndicator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Health health() {
        try {
            rabbitTemplate.execute(channel -> {
                channel.queueDeclarePassive("amq.rabbitmq.reply-to");
                return null;
            });
            return Health.up().withDetail("broker", "RabbitMQ").build();
        } catch (Exception e) {
            return Health.down().withDetail("broker", "RabbitMQ").withException(e).build();
        }
    }
}