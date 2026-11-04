package org.jedi_bachelor.ioboxstarter.configuration

import io.micrometer.core.instrument.MeterRegistry
import org.jedi_bachelor.ioboxstarter.brokers.BrokerStrategy
import org.jedi_bachelor.ioboxstarter.controller.DlqController
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository
import org.jedi_bachelor.ioboxstarter.mapper.DeadLetterMapper
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties
import org.jedi_bachelor.ioboxstarter.scheduler.DlqScheduler
import org.jedi_bachelor.ioboxstarter.service.DlqService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(DlqProperties::class)
@ConditionalOnProperty(name = ["iobox.dlq.enabled"], havingValue = "true")
class DlqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun dlqController(
        dlqService: DlqService,
        deadLetterMapper: DeadLetterMapper
    ): DlqController {
        return DlqController(dlqService, deadLetterMapper)
    }

    @Bean
    @ConditionalOnProperty(
        name = ["iobox.dlq.scheduler.enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    @ConditionalOnMissingBean
    fun dlqScheduler(
        dlqService: DlqService,
        properties: DlqProperties
    ): DlqScheduler {
        return DlqScheduler(properties, dlqService)
    }

    @Bean
    @ConditionalOnMissingBean
    fun dlqService(
        deadLettersRepository: PortDlqRepository,
        brokerStrategy: BrokerStrategy,
        meterRegistry: MeterRegistry
    ): DlqService {
        return DlqService(deadLettersRepository, brokerStrategy, meterRegistry)
    }

    @Bean
    @ConditionalOnMissingBean
    fun deadLetterMapper(): DeadLetterMapper {
        return DeadLetterMapper()
    }
}