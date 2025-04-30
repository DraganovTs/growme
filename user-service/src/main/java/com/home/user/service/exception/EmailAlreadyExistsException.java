package com.home.user.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BaseException {
    public EmailAlreadyExistsException(String message) {
        super(message, "EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}