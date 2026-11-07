package com.example.testapp;

import org.jedi_bachelor.ioboxstarter.annotations.EnableInboxing;
import org.jedi_bachelor.ioboxstarter.annotations.EnableOutboxing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableInboxing
@EnableOutboxing
@EntityScan(basePackages = "org.jedi_bachelor.ioboxstarter.db.entity.jpa") // <-- this only for test application here, in real projects this annotation is excess
//@EnableJpaRepositories(basePackages = "org.jedi_bachelor.ioboxstarter.db.repository.jpa") // <-- this only for test application here, in real projects this annotation is excess
public class TestIoboxApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestIoboxApplication.class, args);
    }
}
