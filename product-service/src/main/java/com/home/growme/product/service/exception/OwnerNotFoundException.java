package com.home.growme.product.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class OwnerNotFoundException extends BaseException {
    public OwnerNotFoundException(String message) {
        super(message, "OWNER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
