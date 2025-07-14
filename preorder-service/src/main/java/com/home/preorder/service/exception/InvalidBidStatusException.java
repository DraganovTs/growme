package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidBidStatusException extends BaseException {
    public InvalidBidStatusException(String message) {
        super(message, "INVALID_STATUS", HttpStatus.BAD_REQUEST);
    }
}
