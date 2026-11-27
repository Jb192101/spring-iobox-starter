package org.jedi_bachelor.ioboxstarter.configuration.db;

import org.jedi_bachelor.ioboxstarter.db.adapter.jpa.*;
import org.jedi_bachelor.ioboxstarter.db.port.PortDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.port.PortOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaDlqRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaInboxRepository;
import org.jedi_bachelor.ioboxstarter.db.repository.jpa.JpaOutboxRepository;
import org.jedi_bachelor.ioboxstarter.db.transaction.IoboxTransactionManager;
import org.jedi_bachelor.ioboxstarter.db.transaction.JpaIoboxTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(name = "iobox.storage", havingValue = "jpa", matchIfMissing = true)
@EnableJpaRepositories(basePackages = "org.jedi_bachelor.ioboxstarter.db.repository.jpa")
@EntityScan(basePackages = "org.jedi_bachelor.ioboxstarter.db.entity.jpa")
@ConditionalOnClass(JpaRepository.class)
public class JpaConfiguration {

  @Bean
  public PortOutboxRepository portJpaOutboxRepository(JpaOutboxRepository repo) {
    return new JpaOutboxAdapter(repo);
  }

  @Bean
  public IoboxTransactionManager jpaIoboxTransactionManager(PlatformTransactionManager transactionManager) {
    return new JpaIoboxTransactionManager(transactionManager);
  }

  @Bean
  public PortInboxRepository portJpaInboxRepository(JpaInboxRepository repo) {
    return new JpaInboxAdapter(repo);
  }

  @Bean
  @ConditionalOnProperty(name = "iobox.dlq.enabled", havingValue = "true")
  public PortDlqRepository portJpaDlqRepository(JpaDlqRepository repo) {
    return new JpaDlqAdapter(repo);
  }

  @Bean
  @ConditionalOnProperty(name = "iobox.dlq.enabled", havingValue = "false", matchIfMissing = true)
  public PortDlqRepository portJpaDlqUnenabledRepository() {
    return new JpaDlqUnenabledAdapter();
  }
}
