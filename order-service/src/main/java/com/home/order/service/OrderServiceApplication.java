package com.home.order.service;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@OpenAPIDefinition(
        info = @Info(
                title = "GrowMe Order Service API",
                version = "1.0.0",
                description = """
                        This microservice is responsible for managing all aspects of order processing in the GrowMe platform.
                        It provides endpoints to:
                        - Create, retrieve, and manage orders
                        - Handle basket/checkout operations
                        - Process payments and payment intents
                        - Manage delivery methods
                        - Track order history for users
                        - Integrate with payment providers and inventory services
                        
                        This service communicates with other GrowMe microservices via Feign clients and Kafka.
                        """,
                contact = @Contact(
                        name = "GrowMe Order Support",
                        email = "orders-support@growme.com",
                        url = "https://support.growme.com/orders"
                ),
                license = @License(
                        name = "GrowMe Platform License",
                        url = "https://www.growme.com/license"
                ),
                termsOfService = "https://www.growme.com/terms/orders"
        ),
        externalDocs = @ExternalDocumentation(
                description = "Full Order Service Documentation",
                url = "https://docs.growme.com/order-service"
        )

)
@EnableFeignClients(basePackages = "com.home.order.service.feign")
@SpringBootApplication
@EnableKafka
@ConfigurationPropertiesScan
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
