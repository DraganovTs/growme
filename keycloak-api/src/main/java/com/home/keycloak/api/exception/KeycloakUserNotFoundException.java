package com.home.keycloak.api.exception;

public class KeycloakUserNotFoundException extends RuntimeException {
    public KeycloakUserNotFoundException(String message) {
        super(message);
    }

    public KeycloakUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
