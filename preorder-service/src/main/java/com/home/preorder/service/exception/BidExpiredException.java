package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class BidExpiredException extends BaseException {
    public BidExpiredException(String message) {
        super(message, "BID_EXPIRED", HttpStatus.GONE);
    }
}
