package com.home.gateway.server.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class TestController {
    @GetMapping("/jwt-info")
    public Mono<Map<String, Object>> getJwtInfo(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> debugInfo = new HashMap<>();

        debugInfo.put("subject", jwt.getSubject());
        debugInfo.put("issuer", jwt.getIssuer());
        debugInfo.put("expiration", jwt.getExpiresAt());
        debugInfo.put("issuedAt", jwt.getIssuedAt());
        debugInfo.put("claims", jwt.getClaims());
        debugInfo.put("headers", jwt.getHeaders());

        return Mono.just(debugInfo);
    }

    @GetMapping("/auth-info")
    public Mono<Map<String, Object>> getAuthInfo(@AuthenticationPrincipal JwtAuthenticationToken authentication) {
        Map<String, Object> authInfo = new HashMap<>();

        authInfo.put("name", authentication.getName());
        authInfo.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        authInfo.put("details", authentication.getDetails());
        authInfo.put("authenticated", authentication.isAuthenticated());

        return Mono.just(authInfo);
    }

    @GetMapping("/test")
    public String test() {
        return "Gateway works fine";
    }
}
