package org.jedi_bachelor.ioboxstarter.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "iobox.inbox")
data class InboxProperties(

    val enabled : Boolean = true,

    val maxRetries : Int = 5,

    val deduplicationEnabled : Boolean = true,

    val retentionDays : Int = 7,

    @NestedConfigurationProperty
    val scheduler : Scheduler = Scheduler()
) {
    data class Scheduler(

        val enabled: Boolean = true,

        val interval : Long = 5000,

        val cleanupCron : String = "0 0 3 * * *"
    )
}