package org.jedi_bachelor.ioboxstarter.configuration;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.zookeeper.curator.ZookeeperCuratorLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@ConditionalOnClass(LockProvider.class)
@ConditionalOnProperty(name = "iobox.shedlock.provider", havingValue = "zookeeper")
public class ShedLockConfiguration {

    @Bean
    public LockProvider lockProvider(CuratorFramework curatorFramework) {
        return new ZookeeperCuratorLockProvider(curatorFramework);
    }
}
