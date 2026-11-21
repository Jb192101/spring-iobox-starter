package org.jedi_bachelor.ioboxstarter.db.transaction;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

public abstract class AbstractTransactionManager implements IoboxTransactionManager {

  protected TransactionTemplate transactionTemplate;

  @Override
  public <T> T execute(Supplier<T> action) {
    return transactionTemplate.execute(status -> action.get());
  }

  @Override
  public void executeWithoutResult(Runnable action) {
    transactionTemplate.executeWithoutResult(status -> action.run());
  }
}
