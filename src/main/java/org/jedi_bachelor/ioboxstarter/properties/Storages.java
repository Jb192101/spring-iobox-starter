package org.jedi_bachelor.ioboxstarter.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Storages {
  JPA("jpa"),

  MONGO("mongo"),

  CASSANDRA("cassandra");

  private final String storageName;
}
