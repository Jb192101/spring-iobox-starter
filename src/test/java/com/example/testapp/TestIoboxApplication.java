package com.example.testapp;

import org.jedi_bachelor.ioboxstarter.annotations.EnableInboxing;
import org.jedi_bachelor.ioboxstarter.annotations.EnableOutboxing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableInboxing
@EnableOutboxing
public class TestIoboxApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestIoboxApplication.class, args);
    }
}
