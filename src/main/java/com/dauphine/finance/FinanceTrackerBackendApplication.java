package com.dauphine.finance;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
	info = @Info(
		title = "Finance Tracker API",
		version = "v1",
		description = "REST API for categories, transactions, and goals."
	)
)
@SpringBootApplication
public class FinanceTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceTrackerBackendApplication.class, args);
	}

}
