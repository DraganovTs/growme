package com.home.mail.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class EmailProcessingException extends BaseException {

    public EmailProcessingException(String message) {
        super(message, "EMAIL_PROCESSING_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
