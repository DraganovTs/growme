package com.home.growme.produt.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistException extends BaseException {
    public CategoryAlreadyExistException(String message) {
        super(message, "CATEGORY_EXIST", HttpStatus.CONFLICT);
    }
}
