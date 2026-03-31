package com.app.bank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Secure Bank API",
                version = "3.0",
                description = "A secure and robust Bank Transaction API built with Spring Boot, Java, and MySQL.\n" +
                        "This project demonstrates best practices for building a backend banking system including account management and transaction processing.",
                contact = @Contact(
                        name = "Franck Ludovic",
                        email = "franckludovic351@gmail.com",
                        url = "https://github.com/ludovicfranck/Transaction-Bank-app-"
                ),
                license = @License(
                        name = "Secure Bank API",
                        url = "https://github.com/ludovicfranck/Transaction-Bank-app-"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Documentation of a secure bank API",
                url = "https://github.com/ludovicfranck/Transaction-Bank-app-"
        )
)
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
