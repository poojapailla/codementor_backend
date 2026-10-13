package com.codementor.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.codementor.backend.config.CaCertBootstrap;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		CaCertBootstrap.ensureAivenCaCert();
		SpringApplication.run(BackendApplication.class, args);
	}

}
