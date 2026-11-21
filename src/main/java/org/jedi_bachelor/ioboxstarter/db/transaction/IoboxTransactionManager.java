package org.jedi_bachelor.ioboxstarter.db.transaction;

import java.util.function.Supplier;

public interface IoboxTransactionManager {
  <T> T execute(Supplier<T> action);

  void executeWithoutResult(Runnable action);
}
