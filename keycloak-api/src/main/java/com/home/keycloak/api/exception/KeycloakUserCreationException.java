package com.home.keycloak.api.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class KeycloakUserCreationException extends BaseException {
    public KeycloakUserCreationException(String message) {
        super(message, "KEYCLOAK_USER_CREATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public KeycloakUserCreationException(String message, Throwable cause) {
        super(message, "KEYCLOAK_USER_CREATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}