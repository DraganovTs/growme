package com.home.user.service.exception;

public class DataConflictException extends RuntimeException {
    private final String errorCode;

    public DataConflictException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
