package com.home.growme.product.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class StockInsufficientException extends BaseException {
    public StockInsufficientException(String message) {
        super(message,"PRODUCT_INSUFFICIENT_STOCK", HttpStatus.CONFLICT);
    }
}
