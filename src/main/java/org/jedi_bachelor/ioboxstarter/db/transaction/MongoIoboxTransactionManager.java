package org.jedi_bachelor.ioboxstarter.db.transaction;

import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class MongoIoboxTransactionManager extends AbstractTransactionManager {

  public MongoIoboxTransactionManager(MongoTransactionManager manager) {
    this.transactionTemplate = new TransactionTemplate(manager);
  }
}
