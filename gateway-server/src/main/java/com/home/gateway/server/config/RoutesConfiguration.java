package com.home.gateway.server.config;

import com.home.gateway.server.filters.UserInfoForwardingFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
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
    private static final String FALLBACK_URI = "forward:/contactSupport";

//    private final UserInfoForwardingFilter userInfoForwardingFilter;

//    public RoutesConfiguration(UserInfoForwardingFilter userInfoForwardingFilter) {
//        this.userInfoForwardingFilter = userInfoForwardingFilter;
//    }

    @Bean
    public RouteLocator growMeRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                // USER-SERVICE - FIXED
                .route("user-service", r -> r.path("/growme/users/**")
                        .filters(f -> applyCommonFilters(f, "userServiceCircuitBreaker")
                                .rewritePath("/growme/users/(?<segment>.*)", "/api/users/${segment}"))
                        .uri("lb://USER-SERVICE"))

                // PRODUCT-SERVICE - FIXED
                .route("product-service-categories-root", p -> p.path("/growme/categories")
                        .filters(f -> applyCommonFilters(f, "productServiceCircuitBreaker")
                                .rewritePath("/growme/categories", "/api/categories"))
                        .uri("lb://PRODUCT-SERVICE"))

                .route("product-service-categories-segment", p -> p.path("/growme/categories/**")
                        .filters(f -> applyCommonFilters(f, "productServiceCircuitBreaker")
                                .rewritePath("/growme/categories/(?<segment>.*)", "/api/categories/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))

                .route("product-service-owners", p -> p.path("/growme/owners/**")
                        .filters(f -> applyCommonFilters(f, "productServiceCircuitBreaker")
                                .rewritePath("/growme/owners/(?<segment>.*)", "/api/owners/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))

                .route("product-service-products-root", p -> p.path("/growme/products")
                        .filters(f -> applyCommonFilters(f, "productServiceCircuitBreaker")
                                .rewritePath("/growme/products", "/api/products"))
                        .uri("lb://PRODUCT-SERVICE"))

                .route("product-service-products-segment", p -> p.path("/growme/products/**")
                        .filters(f -> applyCommonFilters(f, "productServiceCircuitBreaker")
                                .rewritePath("/growme/products/(?<segment>.*)", "/api/products/${segment}"))
                        .uri("lb://PRODUCT-SERVICE"))

                // ORDER-SERVICE - FIXED
                .route("order-service-basket", p -> p.path("/growme/basket/**")
                        .filters(f -> applyCommonFilters(f, "orderServiceCircuitBreaker")
                                .rewritePath("/growme/basket/(?<segment>.*)", "/api/basket/${segment}"))
                        .uri("lb://ORDER-SERVICE"))

                .route("order-service-deliverymethods", p -> p.path("/growme/deliverymethods/**")
                        .filters(f -> applyCommonFilters(f, "orderServiceCircuitBreaker")
                                .rewritePath("/growme/deliverymethods/(?<segment>.*)", "/api/deliverymethods/${segment}"))
                        .uri("lb://ORDER-SERVICE"))

                .route("order-service-orders", p -> p.path("/growme/orders/**")
                        .filters(f -> applyCommonFilters(f, "orderServiceCircuitBreaker")
                                .rewritePath("/growme/orders/(?<segment>.*)", "/api/orders/${segment}"))
                        .uri("lb://ORDER-SERVICE"))

                // KEYCLOAK-ROLE-SERVICE
                .route("keycloak-service-roles", p -> p.path("/growme/roles/**")
                        .filters(f -> applyCommonFilters(f, "keycloakRoleServiceCircuitBreaker")
                                .rewritePath("/growme/roles/(?<segment>.*)", "/api/roles/${segment}"))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))

                .route("keycloak-service-users", p -> p.path("/growme/usersk/**")
                        .filters(f -> applyCommonFilters(f, "keycloakRoleServiceCircuitBreaker")
                                .rewritePath("/growme/usersk/(?<segment>.*)", "/api/usersk/${segment}"))
                        .uri("lb://KEYCLOAK-ROLE-SERVICE"))

                // PRE-ORDER-SERVICE
                .route("preorder-service-tasks-root", p -> p.path("/growme/tasks")
                        .filters(f -> applyCommonFilters(f, "preorderServiceCircuitBreaker")
                                .rewritePath("/growme/tasks", "/api/tasks"))
                        .uri("lb://PREORDER-SERVICE"))

                .route("preorder-service-tasks-segment", p -> p.path("/growme/tasks/**")
                        .filters(f -> applyCommonFilters(f, "preorderServiceCircuitBreaker")
                                .rewritePath("/growme/tasks/(?<segment>.*)", "/api/tasks/${segment}"))
                        .uri("lb://PREORDER-SERVICE"))

                .route("preorder-service-bids-root", p -> p.path("/growme/bids")
                        .filters(f -> applyCommonFilters(f, "preorderServiceCircuitBreaker")
                                .rewritePath("/growme/bids", "/api/bids"))
                        .uri("lb://PREORDER-SERVICE"))

                .route("preorder-service-bids-segment", p -> p.path("/growme/bids/**")
                        .filters(f -> applyCommonFilters(f, "preorderServiceCircuitBreaker")
                                .rewritePath("/growme/bids/(?<segment>.*)", "/api/bids/${segment}"))
                        .uri("lb://PREORDER-SERVICE"))
                .build();
    }

    private GatewayFilterSpec applyCommonFilters(GatewayFilterSpec filterSpec, String circuitBreakerName) {
        return filterSpec
                .circuitBreaker(config -> config.setName(circuitBreakerName)
                        .setFallbackUri(FALLBACK_URI))
                .retry(retryConfig -> retryConfig.setRetries(3)
                        .setMethods(HttpMethod.GET)
                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver()));
    }


    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest()
                        .getHeaders()
                        .getFirst(USER_HEADER))
                .defaultIfEmpty(ANONYMOUS_USER);
    }
}
