package org.jedi_bachelor.ioboxstarter.configuration

import org.jedi_bachelor.ioboxstarter.properties.DlqProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(DlqProperties::class)
class DlqAutoConfiguration {
}