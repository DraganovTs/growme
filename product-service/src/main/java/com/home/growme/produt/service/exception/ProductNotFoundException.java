package com.home.growme.produt.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(String message) {
        super(message, "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
