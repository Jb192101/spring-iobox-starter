package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "iobox")
@Deprecated
public class CommonPropertiesOld {

    private Storages storage;

    private Brokers broker;
}
