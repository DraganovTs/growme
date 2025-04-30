package com.home.growme.common.module.exceptions.security;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends BaseException {
    public UnauthorizedAccessException() {
        super("Authentication required", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}