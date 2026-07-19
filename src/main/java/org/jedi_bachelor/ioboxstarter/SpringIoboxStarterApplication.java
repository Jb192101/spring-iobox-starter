package org.jedi_bachelor.ioboxstarter;

import org.jedi_bachelor.ioboxstarter.annotations.EnableOutboxing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOutboxing
public class SpringIoboxStarterApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringIoboxStarterApplication.class, args);
	}
}
