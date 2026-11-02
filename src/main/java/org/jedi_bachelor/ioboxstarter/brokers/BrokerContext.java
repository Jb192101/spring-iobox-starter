package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.ioboxstarter.db.domain.DlqMessage;
import org.jedi_bachelor.ioboxstarter.db.domain.OutboxMessage;
import org.jedi_bachelor.ioboxstarter.model.MessageEnvelope;
import org.jedi_bachelor.ioboxstarter.properties.Brokers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public final class BrokerContext {

    private final BrokerStrategyFactoryKt factory;

    private BrokerStrategy currentStrategy;

    private final Brokers configuredBroker;

    public BrokerContext(BrokerStrategyFactoryKt factory,
                         @Value("${iobox.broker:kafka}") String brokerType
    ) {
        this.factory = factory;
        this.configuredBroker = Brokers.fromValue(brokerType);
        this.currentStrategy = factory.getStrategy(this.configuredBroker);

        log.info("Initialized broker strategy: {}", this.configuredBroker);
    }

    public void publish(final OutboxMessage message) {
        currentStrategy.publish(message);
    }

    public void consume(final String queueName, final Consumer<MessageEnvelope> consumer) {
        currentStrategy.consume(queueName, consumer);
    }

    public void publishDeadLetter(final DlqMessage message) {
        this.currentStrategy.publishDeadLetter(message);
    }

    public void changeBroker(final Brokers newBroker) {
        if (newBroker != this.configuredBroker) {
            this.currentStrategy = factory.getStrategy(newBroker);

            log.info("Switched broker strategy to: {}", newBroker);
        }
    }
}
