package com.home.user.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BaseException {
    public UsernameAlreadyExistsException(String message) {
        super(message, "USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}