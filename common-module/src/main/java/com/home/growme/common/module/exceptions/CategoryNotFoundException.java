package com.home.growme.common.module.exceptions;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {
    public CategoryNotFoundException(String message) {
        super(message, "CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
