package com.home.growme.produt.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class OwnerAlreadyExistsException extends BaseException {
    public OwnerAlreadyExistsException(String message) {
        super(message, "OWNER_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}
