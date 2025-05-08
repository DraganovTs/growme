package com.home.order.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class DeliveryMethodNotFoundException extends BaseException {
    public DeliveryMethodNotFoundException(String message) {
        super(message, "DELIVERY_METHOD_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
