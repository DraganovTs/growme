package com.home.growme.common.module.exceptions;

import org.springframework.http.HttpStatus;

public class EmailProcessingException extends BaseException {
    public EmailProcessingException(String message) {
        super(message, "EMAIL_PROCESSING_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
