package com.asdin.test_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TestRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestRestApplication.class, args);
	}

}
