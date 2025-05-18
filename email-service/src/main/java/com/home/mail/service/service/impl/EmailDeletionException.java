package com.home.mail.service.service.impl;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class EmailDeletionException extends BaseException {
    public EmailDeletionException(String message) {
        super(message, "DELETION_MAIL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
