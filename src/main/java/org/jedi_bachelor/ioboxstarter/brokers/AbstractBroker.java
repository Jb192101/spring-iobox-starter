package org.jedi_bachelor.ioboxstarter.brokers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jedi_bachelor.ioboxstarter.properties.DlqProperties;

@Getter
@Setter
@AllArgsConstructor
abstract public class AbstractBroker {
    protected DlqProperties dlqProperties;
}
