package com.huda;

import org.springframework.boot.SpringApplication;

public class TestConsumerApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsumerApiServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
