package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class BidNotFoundException extends BaseException {


    public BidNotFoundException(String message) {
        super(message, "BID_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
