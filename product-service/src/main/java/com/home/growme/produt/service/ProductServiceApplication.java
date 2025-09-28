package com.home.growme.produt.service;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;


@SpringBootApplication
@EnableRetry
@OpenAPIDefinition(
        info = @Info(
                title = "GrowMe Product Service API",
                version = "1.0.0",
                description = """
            This microservice is responsible for managing all aspects of product data in the GrowMe platform.
            It provides endpoints to:
            - Create, retrieve, update, and delete products
            - Upload and retrieve product images
            - Manage product categories
            - Fetch seller-specific products
            - Validate product availability for orders
            - Support pagination, sorting, and filtering on product queries
            
            This service communicates with other GrowMe microservices via Kafka and REST.
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
                description = "Full Product Service Documentation",
                url = "https://docs.growme.com/product-service"
        )
)
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
