package com.home.growme.common.module.exceptions.data;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException(String field) {
        super("Invalid value for field: " + field,
                "INVALID_REQUEST",
                HttpStatus.BAD_REQUEST);
    }
}