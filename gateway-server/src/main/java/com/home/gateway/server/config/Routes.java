package com.home.gateway.server.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    @Bean
    public RouteLocator growMeRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                // USER-SERVICE
                .route("user-service",r -> r.path("/growme/api/users/**")
                        .filters(f -> f.rewritePath("/growme/api/users/(?<segment>.*)", "/${segment}"))
                        .uri("lb://USER-SERVICE"))

                // PRODUCT-SERVICE
                .route(p -> p.path("/growme/api/categories/**")
                        .filters(f -> f.rewritePath("/growme/api/categories/(?<segment>.*)", "/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))
                .route(p -> p.path("/growme/api/owners/**")
                        .filters(f -> f.rewritePath("/growme/api/owners/(?<segment>.*)", "/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))
                .route(p -> p.path("/growme/api/products/**")
                        .filters(f -> f.rewritePath("/growme/api/products/(?<segment>.*)", "/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))

                // ORDER-SERVICE
                .route(p -> p.path("/growme/api/basket/**")
                        .filters(f -> f.rewritePath("/growme/api/basket/(?<segment>.*)", "/${segment}"))
                        .uri("lb://ORDER-SERVICE"))
                .route(p -> p.path("/growme/deliverymethods/**")
                        .filters(f -> f.rewritePath("/growme/deliverymethods/?(?<segment>.*)", "/api/deliverymethods/${segment}"))
                        .uri("lb://ORDER-SERVICE"))
                .route(p -> p.path("/growme/api/orders/**")
                        .filters(f -> f.rewritePath("/growme/api/orders/(?<segment>.*)", "/${segment}"))
                        .uri("lb://ORDER-SERVICE"))

                //KEYCLOAK-ROLE-SERVICE
                .route(p -> p.path("/growme/api/roles/**")
                        .filters(f -> f.rewritePath("/growme/api/roles/(?<segment>.*)", "/${segment}"))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))
                .route(p -> p.path("/growme/api/usersk/**")
                        .filters(f -> f.rewritePath("/growme/api/usersk/(?<segment>.*)", "/${segment}"))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))
                .build();
    }
}
