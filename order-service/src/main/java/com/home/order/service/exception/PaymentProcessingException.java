package com.home.order.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentProcessingException extends BaseException {

    public PaymentProcessingException(String message) {
        super(message, "PAYMENT_PROCESSING_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
