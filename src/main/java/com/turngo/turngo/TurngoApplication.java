package com.turngo.turngo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TurngoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurngoApplication.class, args);
	}

}
