package com.home.gateway.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtEventListener {

    private static final Logger log = LoggerFactory.getLogger(JwtEventListener.class);

    @EventListener
    public void handleJwtValidated(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) event.getAuthentication();
            Jwt jwt = jwtAuth.getToken();

            log.info("JWT Authentication Successful:");
            log.info("  Subject: {}", jwt.getSubject());
            log.info("  Expires: {}", jwt.getExpiresAt());
            log.info("  Roles: {}", jwt.getClaimAsStringList("realm_access.roles"));
        }
    }

    @EventListener
    public void handleJwtError(AbstractAuthenticationFailureEvent event) {
        log.error("JWT Authentication Failed: {}", event.getException().getMessage());
    }
}
