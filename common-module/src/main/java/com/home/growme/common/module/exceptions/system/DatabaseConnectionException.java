package com.home.growme.common.module.exceptions.system;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class DatabaseConnectionException extends BaseException {
    public DatabaseConnectionException(String message) {
        super(message, "DATABASE_CONNECTION_FAILED", HttpStatus.SERVICE_UNAVAILABLE);
    }
}