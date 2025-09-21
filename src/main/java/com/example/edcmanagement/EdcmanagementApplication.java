package com.example.edcmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EdcmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdcmanagementApplication.class, args);
	}

}
