package org.jedi_bachelor.ioboxstarter.configuration.brokers;

import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy;
import org.jedi_bachelor.ioboxstarter.brokers.RedisBrokerStrategy;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@AutoConfiguration
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(name = "iobox.broker", havingValue = "redis")
@EnableConfigurationProperties(DlqProperties.class)
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(BrokerStrategy.class)
    public BrokerStrategy redisBrokerStrategy(RedisTemplate<String, String> redisTemplate, DlqProperties dlqProperties) {
        return new RedisBrokerStrategy(dlqProperties, redisTemplate);
    }
}
