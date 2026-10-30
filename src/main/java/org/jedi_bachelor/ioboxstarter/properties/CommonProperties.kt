package org.jedi_bachelor.ioboxstarter.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "iobox")
data class CommonProperties(

    val broker: Brokers,

    val storage: Storages
) {
}