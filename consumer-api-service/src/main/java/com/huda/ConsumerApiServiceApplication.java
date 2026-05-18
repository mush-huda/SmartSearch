package com.huda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ConsumerApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApiServiceApplication.class, args);
	}

}
