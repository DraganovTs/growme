package com.home.gateway.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Configuration
public class RoutesConfiguration {



    private static final String USER_HEADER = "user";
    private static final String ANONYMOUS_USER = "anonymous";

    @Bean
    public RouteLocator growMeRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                // USER-SERVICE
                .route("user-service", r -> r.path("/growme/users/**")
                        .filters(f -> f.rewritePath("/growme/users/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(config -> config.setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://USER-SERVICE"))

                // PRODUCT-SERVICE
                .route("product-service-categories", p -> p.path("/growme/categories/**")
                        .filters(f -> f.rewritePath("/growme/categories/(?<segment>.*)", "/api/categories/${segment}")
                                .circuitBreaker(config -> config.setName("productServiceCircuitBreaker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("product-service-owners", p -> p.path("/growme/owners/**")
                        .filters(f -> f.rewritePath("/growme/owners/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(config -> config.setName("productServiceCircuitBraker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://PRODUCT-SERVICE"))
                .route("product-service-products", p -> p.path("/growme/products/**")
                        .filters(f -> f.rewritePath("/growme/products/(?<segment>.*)", "/api/products/${segment}")
                                .circuitBreaker(config -> config.setName("productServiceCircuitBraker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://PRODUCT-SERVICE"))

                // ORDER-SERVICE
                .route("order-service-basket", p -> p.path("/growme/basket/**")
                        .filters(f -> f.rewritePath("/growme/basket(?<segment>/?.*)", "/api/basket${segment}")
                                .circuitBreaker(config -> config.setName("orderServiceCircuitBraker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://ORDER-SERVICE"))
                .route("order-service-deliverymethods", p -> p.path("/growme/deliverymethods/**")
                        .filters(f -> f.rewritePath("/growme/deliverymethods/?(?<segment>/?.*)", "/api/deliverymethods${segment}")
                                .circuitBreaker(config -> config.setName("orderServiceCircuitBraker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://ORDER-SERVICE"))
                .route("order-service-orders", p -> p.path("/growme/orders/**", "/growme/orders/**")
                        .filters(f -> f.rewritePath("/growme/orders(?<segment>/?.*)", "/api/orders${segment}")
                                .addResponseHeader("X-Response-Time", new Date().toString())
                                .circuitBreaker(config -> config.setName("orderServiceCircuitBraker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://ORDER-SERVICE"))

                //KEYCLOAK-ROLE-SERVICE
                .route("keycloak-service-roles",p -> p.path("/growme/roles/**")
                        .filters(f -> f.rewritePath("/growme/roles/(?<segment>.*)", "/api/roles/${segment}")
                                .circuitBreaker(config -> config.setName("keycloakRoleServiceCircuitBraker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))
                .route("keycloak-service-users",p -> p.path("/growme/usersk/**")
                        .filters(f -> f.rewritePath("/growme/usersk/(?<segment>.*)", "/api/usersk/${segment}")
                                .circuitBreaker(config -> config.setName("keycloakRoleServiceCircuitBraker")
                                        .setFallbackUri("forward:/contactSupport"))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest()
                        .getHeaders()
                        .getFirst(USER_HEADER))
                .defaultIfEmpty(ANONYMOUS_USER);
    }
}
