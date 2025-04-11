package com.home.user.service.exception;

public class EmailAlreadyExistsException extends DataConflictException {

    public EmailAlreadyExistsException(String email) {
        super("EMAIL_EXISTS", "Email already in use: " + email);
    }
}
