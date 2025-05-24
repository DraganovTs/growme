package com.home.order.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class OrderNotfoundException extends BaseException {
    public OrderNotfoundException(String message) {
        super(message, "ORDER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
