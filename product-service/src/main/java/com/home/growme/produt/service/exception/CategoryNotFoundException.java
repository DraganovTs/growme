package com.home.growme.produt.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {
    public CategoryNotFoundException(String message) {
        super(message, "CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
