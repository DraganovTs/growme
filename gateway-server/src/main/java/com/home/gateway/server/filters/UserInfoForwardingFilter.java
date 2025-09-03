package com.home.gateway.server.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserInfoForwardingFilter extends AbstractGatewayFilterFactory<UserInfoForwardingFilter.Config> {


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(Jwt.class)
                .map(jwt -> addUserHeaders(exchange, jwt))
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private ServerWebExchange addUserHeaders(ServerWebExchange exchange, Jwt jwt) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .header("X-User-ID", jwt.getSubject())
                .header("X-User-Name", getClaim(jwt, "preferred_username"))
                .header("X-User-Email", getClaim(jwt, "email"))
                .header("X-User-Roles", String.join(",", getRoles(jwt)))
                .build();

        return exchange.mutate().request(request).build();
    }

    private String getClaim(Jwt jwt, String claimName) {
        return jwt.hasClaim(claimName) ? jwt.getClaimAsString(claimName) : "";
    }

    private List<String> getRoles(Jwt jwt) {
        if (jwt.hasClaim("realm_access")) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                return roles != null ? roles : Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }


    public static class Config{

    }
}
