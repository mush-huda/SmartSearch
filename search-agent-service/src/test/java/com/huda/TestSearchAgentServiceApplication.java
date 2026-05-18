package com.huda;

import org.springframework.boot.SpringApplication;

public class TestSearchAgentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(SearchAgentServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
