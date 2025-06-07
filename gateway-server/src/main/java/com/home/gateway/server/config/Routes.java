package com.home.gateway.server.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class Routes {

    @Bean
    public RouteLocator growMeRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                // USER-SERVICE
                .route("user-service",r -> r.path("/growme/users/**")
                        .filters(f -> f.rewritePath("/growme/users/(?<segment>.*)", "/${segment}"))
                        .uri("lb://USER-SERVICE"))

                // PRODUCT-SERVICE
                .route("product-service" ,p -> p.path("/growme/categories/**")
                        .filters(f -> f.rewritePath("/growme/categories/(?<segment>.*)", "/api/categories/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("product-service",p -> p.path("/growme/owners/**")
                        .filters(f -> f.rewritePath("/growme/owners/(?<segment>.*)", "/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("product-service",p -> p.path("/growme/products/**")
                        .filters(f -> f.rewritePath("/growme/products/(?<segment>.*)", "/api/products/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))

                // ORDER-SERVICE
                .route("order-service",p -> p.path("/growme/basket/**")
                        .filters(f -> f.rewritePath("/growme/basket(?<segment>/?.*)", "/api/basket${segment}"))
                        .uri("lb://ORDER-SERVICE"))
                .route("order-service",p -> p.path("/growme/deliverymethods/**")
                        .filters(f -> f.rewritePath("/growme/deliverymethods/?(?<segment>/?.*)", "/api/deliverymethods${segment}"))
                        .uri("lb://ORDER-SERVICE"))
                .route("order-service",p -> p.path("/growme/orders/**","/growme/orders/**")
                        .filters(f -> f.rewritePath("/growme/orders(?<segment>/?.*)", "/api/orders${segment}")
                                .addResponseHeader("X-Response-Time",new Date().toString()))
                        .uri("lb://ORDER-SERVICE"))

                //KEYCLOAK-ROLE-SERVICE
                .route(p -> p.path("/growme/roles/**")
                        .filters(f -> f.rewritePath("/growme/roles/(?<segment>.*)", "/api/roles/${segment}"))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))
                .route(p -> p.path("/growme/usersk/**")
                        .filters(f -> f.rewritePath("/growme/usersk/(?<segment>.*)", "/api/usersk/${segment}"))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))
                .build();
    }
}
