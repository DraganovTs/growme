package com.home.user.service;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Main entry point for the User Service application.
 *
 * <p>This microservice handles all user-related operations including:
 * <ul>
 *   <li>User registration and profile management</li>
 *   <li>Authentication and authorization integration</li>
 *   <li>User data persistence and retrieval</li>
 * </ul>
 *
 * <p>The service exposes a REST API documented using OpenAPI 3.0 specification.
 * Access the interactive API documentation at {@code /swagger-ui.html} when the
 * application is running.
 *
 * @see <a href="https://docs.growme.com/user-service">User Service Documentation</a>
 */
@SpringBootApplication
@EnableRetry
@OpenAPIDefinition(
        info = @Info(
                title = "User Service API",
                version = "1.0.0",
                description = """
                        Comprehensive API for managing user accounts and profiles in the GrowMe platform.
                        Provides endpoints for user registration, authentication, profile management,
                        and account operations.
                        """,
                contact = @Contact(
                        name = "GrowMe Developer Support",
                        email = "dev-support@growme.com",
                        url = "https://support.growme.com"
                ),
                license = @License(
                        name = "GrowMe Platform License",
                        url = "https://www.growme.com/license"
                ),
                termsOfService = "https://www.growme.com/terms"
        ),
        externalDocs = @ExternalDocumentation(
                description = "User Service Implementation Guide",
                url = "https://docs.growme.com/user-service/guide"
        )
)
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
