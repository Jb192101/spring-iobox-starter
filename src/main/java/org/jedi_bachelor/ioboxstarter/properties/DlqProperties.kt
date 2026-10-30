package org.jedi_bachelor.ioboxstarter.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "iobox.dlq")
data class DlqProperties(

    val enabled: Boolean = false,

    val dlqName: String = "dead-letters",

    @NestedConfigurationProperty
    val scheduler: Scheduler = Scheduler()
) {
    data class Scheduler(

        val enabled: Boolean = true,

        val interval: Long = 5000
    )
}