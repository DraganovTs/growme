package com.home.user.service.exception;

public class NotValidUserRoleException extends RuntimeException {
    public NotValidUserRoleException(String message) {
        super(message);
    }
}
