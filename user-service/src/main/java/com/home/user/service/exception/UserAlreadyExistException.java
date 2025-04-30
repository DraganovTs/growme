package com.home.user.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistException extends BaseException {
    public UserAlreadyExistException(String message) {
        super(message, "USER_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}