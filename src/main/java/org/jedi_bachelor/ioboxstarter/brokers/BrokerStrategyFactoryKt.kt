package org.jedi_bachelor.ioboxstarter.brokers

import org.jedi_bachelor.ioboxstarter.properties.Brokers
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jms.core.JmsTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class BrokerStrategyFactoryKt(

    private val dlqProperties: DlqProperties,

    private val kafkaTemplate: KafkaTemplate<String, Any>,

    private val jmsTemplate: JmsTemplate,

    @Qualifier("redisTemplate") private val redisTemplate: RedisTemplate<String, String>,

    private val rabbitTemplate: RabbitTemplate
) {

    fun getStrategy(brokerType: Brokers) : BrokerStrategy {
        return when(brokerType) {
            Brokers.KAFKA -> getKafkaStrategy()
            Brokers.RABBIT -> getRabbitMqStrategy()
            Brokers.ACTIVEMQ -> getActiveMqStrategy()
            Brokers.REDIS -> getRedisStrategy()
        }
    }

    private fun getKafkaStrategy(): KafkaBrokerStrategy {
        return KafkaBrokerStrategy(kafkaTemplate, dlqProperties)
    }

    private fun getRabbitMqStrategy(): RabbitMQBrokerStrategy {
        return RabbitMQBrokerStrategy(rabbitTemplate, dlqProperties)
    }

    private fun getRedisStrategy(): RedisBrokerStrategy {
        return RedisBrokerStrategy(dlqProperties, redisTemplate)
    }

    private fun getActiveMqStrategy(): ActiveMQBrokerStrategy {
        return ActiveMQBrokerStrategy(jmsTemplate, dlqProperties)
    }
}