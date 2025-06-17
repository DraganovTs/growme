# 🌱 GrowMe - Microservices E-commerce Platform

GrowMe is a modern microservices-based e-commerce system designed to connect buyers with local sellers of fresh produce, homemade products, and natural beverages. Built using Spring Boot, Spring Cloud, Kafka, Redis, and Keycloak for secure and scalable distributed architecture.

---

## 🚀 Features

- 🛒 **Product Service** – Manages products, categories, and images.
- 👥 **User Service** – Manages user profiles and roles via Keycloak.
- 📦 **Order Service** – Handles order creation and tracking.
- 💳 **Payment Service** – Integrates Stripe for secure payments.
- 📧 **Email Service** – Sends email notifications (SendGrid).
- 🔑 **Keycloak API** – Syncs and manages Keycloak users and roles.
- 🌐 **Gateway Server** – API Gateway using Spring Cloud Gateway.
- 🧭 **Eureka Server** – Service discovery and registry.
- ⚙️ **Config Server** – Centralized configuration for all services.
- 📡 **Kafka Integration** – Asynchronous communication between services.
- 📊 **Redis Integration** – Caching and token/session management.

---

## 🧱 Tech Stack

| Layer             | Technology                                                                 |
|------------------|------------------------------------------------------------------------------|
| Core Framework    | Spring Boot 3.4.1                                                            |
| Microservices     | Spring Cloud, Eureka, Gateway, Config Server                                |
| Messaging         | Apache Kafka (Spring Kafka 3.3.4)                                           |
| Security          | Keycloak, OAuth2, JWT                                                       |
| Database          | MySQL (Connector 8.0.33), Spring Data JPA                                   |
| Caching           | Redis (Spring Data Redis, Jedis)                                            |
| Payment           | Stripe Java SDK                                                             |
| Email             | SendGrid Java SDK                                                           |
| Documentation     | SpringDoc OpenAPI (Swagger UI)                                              |
| Dockerization     | Jib Maven Plugin                                                            |
| Communication     | Feign Clients, Kafka, REST APIs                                             |

---

## 🧩 Modules
growme/
│
├── product-service # Product management
├── order-service # Order processing
├── user-service # User info & Keycloak integration
├── keycloak-api # Role sync & Keycloak automation
├── payment-service # Stripe integration
├── email-service # Email notification service
├── config-server # Spring Cloud Config Server
├── eureka-server # Service discovery
├── gateway-server # API Gateway (Spring Cloud Gateway)
├── common-module # Shared models & utilities
└── pom.xml # Parent POM

## ⚙️ How to Run

> 💡 Prerequisites:
> - Java 21
> - Docker & Docker Compose
> - Maven 3.8+
> - Keycloak 26.x (can be containerized)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/draganovTs/growme.git
   cd growme

2. **Start infrastructure (Kafka, MySQL, Redis, Keycloak)**
docker-compose up -d

3. **Start Config, Eureka, and Gateway servers::**
cd config-server && mvn spring-boot:run
cd ../eureka-server && mvn spring-boot:run
cd ../gateway-server && mvn spring-boot:run


3. **Start Config, Eureka, and Gateway servers::**

