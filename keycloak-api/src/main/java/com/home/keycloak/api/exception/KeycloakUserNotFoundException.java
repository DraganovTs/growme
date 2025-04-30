package com.home.keycloak.api.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class KeycloakUserNotFoundException extends BaseException {
    public KeycloakUserNotFoundException(String message) {
        super(message, "KEYCLOAK_USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public KeycloakUserNotFoundException(String message, Throwable cause) {
        super(message, "KEYCLOAK_USER_NOT_FOUND", HttpStatus.NOT_FOUND, cause);
    }
}
