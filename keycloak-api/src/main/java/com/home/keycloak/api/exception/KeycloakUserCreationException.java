package com.home.keycloak.api.exception;

public class KeycloakUserCreationException extends RuntimeException {
    public KeycloakUserCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakUserCreationException(String message) {
        super(message);
    }
}
