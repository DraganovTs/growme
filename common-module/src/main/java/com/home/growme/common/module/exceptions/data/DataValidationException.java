package com.home.growme.common.module.exceptions.data;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class DataValidationException extends BaseException {
    public DataValidationException(String message) {
        super(message, "DATA_VALIDATION_FAILED", HttpStatus.BAD_REQUEST);
    }
}
