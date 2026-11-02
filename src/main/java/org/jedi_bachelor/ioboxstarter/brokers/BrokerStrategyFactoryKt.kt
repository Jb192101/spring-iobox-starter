package org.jedi_bachelor.ioboxstarter.brokers

import org.jedi_bachelor.ioboxstarter.properties.Brokers
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.ObjectProvider
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jms.core.JmsTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class BrokerStrategyFactoryKt(

    private val dlqProperties: DlqProperties,

    private val kafkaTemplateProvider: ObjectProvider<KafkaTemplate<String, Any>>,

    private val jmsTemplateProvider: ObjectProvider<JmsTemplate>,

    private val redisTemplateProvider: ObjectProvider<RedisTemplate<String, String>>,

    private val rabbitTemplateProvider: ObjectProvider<RabbitTemplate>
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
        val template = kafkaTemplateProvider.getIfAvailable()
            ?: throw IllegalStateException("KafkaTemplate is not available. Please add spring-kafka dependency.")

        return KafkaBrokerStrategy(template, dlqProperties)
    }

    private fun getRabbitMqStrategy(): RabbitMQBrokerStrategy {
        val template = rabbitTemplateProvider.getIfAvailable()
            ?: throw IllegalStateException("RabbitTemplate is not available. Please add spring-boot-starter-amqp dependency.")

        return RabbitMQBrokerStrategy(template, dlqProperties)
    }

    private fun getRedisStrategy(): RedisBrokerStrategy {
        val template = redisTemplateProvider.getIfAvailable()
            ?: throw IllegalStateException("RedisTemplate is not available. Please add spring-boot-starter-data-redis dependency.")

        return RedisBrokerStrategy(dlqProperties, template)
    }

    private fun getActiveMqStrategy(): ActiveMQBrokerStrategy {
        val template = jmsTemplateProvider.getIfAvailable()
            ?: throw IllegalStateException("JmsTemplate is not available. Please add spring-boot-starter-activemq dependency.")

        return ActiveMQBrokerStrategy(template, dlqProperties)
    }
}