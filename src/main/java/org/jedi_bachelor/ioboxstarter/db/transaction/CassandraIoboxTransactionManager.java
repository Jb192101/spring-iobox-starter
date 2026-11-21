package org.jedi_bachelor.ioboxstarter.db.transaction;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CassandraIoboxTransactionManager extends AbstractTransactionManager {

  public CassandraIoboxTransactionManager() {
    log.warn(
        "Cassandra doesn't support Spring Transactions. All operations will be executed without rollback capability.");
  }

  @Override
  public <T> T execute(Supplier<T> action) {
      log.warn("This code executed without transaction wrapper!");
      return action.get();
  }

  @Override
  public void executeWithoutResult(Runnable action) {
      log.warn("This code executed without transaction wrapper!");
      action.run();
  }
}
