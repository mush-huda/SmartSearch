package com.huda;

import org.springframework.boot.SpringApplication;

public class TestSearchMcpServerApplication {

	public static void main(String[] args) {
		SpringApplication.from(SearchMcpServerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
