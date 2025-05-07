package com.home.order.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidProductException extends BaseException {

    public InvalidProductException(String message) {
        super(message, "INVALID_PRODUCT", HttpStatus.BAD_REQUEST);
    }
}
