package com.home.order.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentFailedException extends BaseException {

    public PaymentFailedException(String message) {
        super(message, "PAYMENT_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
