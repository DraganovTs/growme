package com.home.growme.common.module.exceptions.security;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends BaseException {
    public ForbiddenOperationException(String operation) {
        super("Not allowed to perform: " + operation,
                "FORBIDDEN_OPERATION",
                HttpStatus.FORBIDDEN);
    }
}