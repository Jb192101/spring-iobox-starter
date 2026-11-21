package org.jedi_bachelor.ioboxstarter.db.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class JpaIoboxTransactionManager extends AbstractTransactionManager {

  public JpaIoboxTransactionManager(PlatformTransactionManager manager) {
    this.transactionTemplate = new TransactionTemplate(manager);
  }
}
