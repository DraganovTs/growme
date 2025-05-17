package com.home.mail.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class EmailSendingException extends BaseException {

    public EmailSendingException(String message) {
        super(message, "EMAIL_SENDING_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
