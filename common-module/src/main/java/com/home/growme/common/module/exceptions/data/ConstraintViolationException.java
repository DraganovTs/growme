package com.home.growme.common.module.exceptions.data;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ConstraintViolationException extends BaseException {
    public ConstraintViolationException(String constraint) {
        super("Constraint violated: " + constraint,
                "CONSTRAINT_VIOLATION",
                HttpStatus.BAD_REQUEST);
    }
}