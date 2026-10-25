package org.jedi_bachelor.ioboxstarter.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "iobox.outbox")
data class OutboxProperties(

    val enabled: Boolean = true,

    val maxRetries: Int = 5,

    val retryDelay: Long = 5000,

    val deduplicationEnabled: Boolean = true,

    val retentionDays: Int = 7,

    @NestedConfigurationProperty
    val scheduler: Scheduler = Scheduler(),

    @NestedConfigurationProperty
    val contextManager: ContextManager = ContextManager()
) {
    data class Scheduler(

        val interval: Long = 5000,

        val cleanupCron: String = "0 0 3 * * *",

        val enabled: Boolean = true
    )

    data class ContextManager(

        val enabled: Boolean = true,

        val defaultTopic: String = "default"
    )
}