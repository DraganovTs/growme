package com.home.user.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class NotValidUserRoleException extends BaseException {
    public NotValidUserRoleException(String message) {
        super(message, "INVALID_USER_ROLE", HttpStatus.BAD_REQUEST);
    }
}
