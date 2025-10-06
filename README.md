# 🌱 GrowMe - Microservices E-commerce Platform
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
GrowMe is a modern, cloud-native microservices e-commerce platform designed to connect buyers with local sellers of fresh produce, homemade products, and natural beverages. Built with Spring Boot ecosystem and enterprise-grade technologies for scalability, security, and maintainability.
## 🎯 Overview
This platform demonstrates a complete microservices architecture with:
- **Event-driven communication** using Apache Kafka
- **Centralized authentication** with Keycloak
- **API Gateway** for unified access
- **Service discovery** with Eureka
- **Distributed caching** with Redis
- **Secure payments** via Stripe integration
- **Containerized deployment** with Docker
## ✨ Key Features
### 🛍️ Core Business Services
- **Product Catalog** - Comprehensive product, category, and inventory management
- **Order Management** - Complete order lifecycle from creation to fulfillment
- **User Profiles** - Customer and seller account management
- **Payment Processing** - Secure payment handling with Stripe integration
- **Email Notifications** - Automated email communications via SendGrid
### 🏗️ Infrastructure Services
- **API Gateway** - Single entry point with routing and load balancing
- **Service Registry** - Dynamic service discovery and health monitoring
- **Configuration Server** - Centralized configuration management
- **Authentication Service** - OAuth2/JWT-based security with Keycloak
### 🔧 Cross-cutting Concerns
- **Distributed Messaging** - Asynchronous communication with Kafka
- **Caching Layer** - Performance optimization with Redis
- **Monitoring & Logging** - Comprehensive observability
- **Documentation** - Interactive API docs with Swagger UI
## 🏛️ Architecture
### Microservices Overview
### Technology Stack
| Component | Technology | Version |
|-----------|------------|---------|
| **Framework** | Spring Boot | 3.4.1 |
| **Microservices** | Spring Cloud | 2024.0.x |
| **Message Broker** | Apache Kafka | 3.3.4 |
| **Security** | Keycloak, OAuth2, JWT | 26.x |
| **Database** | MySQL | 8.0.33 |
| **Caching** | Redis | Latest |
| **API Gateway** | Spring Cloud Gateway | Latest |
| **Service Discovery** | Netflix Eureka | Latest |
| **Payment** | Stripe Java SDK | Latest |
| **Email** | SendGrid | Latest |
| **Documentation** | SpringDoc OpenAPI | Latest |
| **Containerization** | Docker, Jib | Latest |
## 📁 Project Structure
growme/ ├── 🏗️ Infrastructure Services │
├── config-server/ # Centralized configuration │
├── eureka-server/ # Service discovery
│── gateway-server/ # API Gateway
├── 🛒 Business Services │
├── product-service/ # Product & inventory management │
├── order-service/ # Order processing │
├── preorder-service/ # Pre-order handling │
├── user-service/ # User profile management │
├── payment-service/ # Payment processing
│── email-service/ # Email notifications
├── 🔐 Security & Integration │
├── keycloak-api/ # Keycloak integration
│── common-module/ # Shared utilities & models
├── 🖥️ Frontend │
└── growme-client/ # Angular frontend application
├── 🐳 Deployment │
├── docker/ # Docker configurations │
├── docker-compose/ # Compose files & configs │
└── uploads/ # File storage
└── 📊 Data
└── kafka-volumes/ # Kafka persistent storage

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

## 🚀 Quick Start
### Prerequisites
Ensure you have the following installed:
- ☕ **Java 21** - [Download](https://openjdk.java.net/projects/jdk/21/)
- 🏗️ **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- 🐳 **Docker & Docker Compose** - [Download](https://www.docker.com/get-started)
- 🎯 **Git** - [Download](https://git-scm.com/downloads)
### Installation & Setup
1. **Clone the repository**
   ```bash
   git clone https://github.com/draganovTs/growme.git
   cd growme
   ```
2. **Start Infrastructure Services**
   ```bash
   # Start all infrastructure components (MySQL, Redis, Kafka, Keycloak)
   cd docker-compose
   docker-compose -f docker-compose-infrastructure.yml up -d
   
   # Wait for services to be ready (approximately 2-3 minutes)
   docker-compose logs -f
   ```
3. **Start Core Services** (in separate terminals)
   ```bash
   # Terminal 1: Configuration Server
   cd config-server
   mvn spring-boot:run
   
   # Terminal 2: Service Discovery
   cd eureka-server  
   mvn spring-boot:run
   
   # Terminal 3: API Gateway
   cd gateway-server
   mvn spring-boot:run
   ```
4. **Start Business Services**
   ```bash
   # Terminal 4: Product Service
   cd product-service
   mvn spring-boot:run
   
   # Terminal 5: User Service  
   cd user-service
   mvn spring-boot:run
   
   # Terminal 6: Order Service
   cd order-service
   mvn spring-boot:run
   
   # Continue with other services as needed...
   ```
5. **Start Frontend** (optional)
   ```bash
   cd growme-client
   npm install
   ng serve
   ```
## 🌐 Service URLs
| Service | URL | Description |
|---------|-----|-------------|
| **API Gateway** | http://localhost:8080 | Main application entry point |
| **Eureka Dashboard** | http://localhost:8761 | Service registry console |
| **Keycloak Admin** | http://localhost:8181 | Identity management |
| **Product Service** | http://localhost:8081/api/products | Product management |
| **Order Service** | http://localhost:8082/api/orders | Order processing |
| **User Service** | http://localhost:8083/api/users | User management |
| **Frontend App** | http://localhost:4200 | Angular client |
## 📚 API Documentation
Interactive API documentation is available through Swagger UI:
- **Gateway Swagger**: http://localhost:8080/swagger-ui.html
- **Product API**: http://localhost:8081/swagger-ui.html
- **Order API**: http://localhost:8082/swagger-ui.html
- **User API**: http://localhost:8083/swagger-ui.html
## 🔧 Development
### Running Tests
```bash
# Run all tests
mvn clean test
# Run tests for specific service
cd product-service
mvn test
```