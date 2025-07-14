package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends BaseException {
    public UnauthorizedActionException(String message) {
        super(message, "UNAUTHORIZED_ACTION", HttpStatus.UNAUTHORIZED);
    }
}
