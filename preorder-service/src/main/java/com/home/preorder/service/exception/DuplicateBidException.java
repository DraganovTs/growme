package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateBidException extends BaseException {
    public DuplicateBidException(String message) {

        super(message, "DUPLICATE_BID", HttpStatus.CONFLICT);
    }
}
