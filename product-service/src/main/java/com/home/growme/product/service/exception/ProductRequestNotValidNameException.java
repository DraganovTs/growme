package com.home.growme.product.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ProductRequestNotValidNameException extends BaseException {
    public ProductRequestNotValidNameException(String message) {
        super(message, "INVALID_PRODUCT_NAME", HttpStatus.BAD_REQUEST);
    }
}
