package com.home.order.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class BasketNotFoundException extends BaseException {
    public BasketNotFoundException(String message) {
        super(message, "BASKET_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
