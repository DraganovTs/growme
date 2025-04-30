package com.home.growme.common.module.exceptions.security;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class TokenValidationException extends BaseException {
    public TokenValidationException(String reason) {
        super("Token validation failed: " + reason,
                "INVALID_TOKEN",
                HttpStatus.UNAUTHORIZED);
    }
}