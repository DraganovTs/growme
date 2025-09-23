package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class TaskCategoryAlreadyExistException extends BaseException {

    public TaskCategoryAlreadyExistException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, "TASK_CATEGORY_ALREADY_EXIST", HttpStatus.CONFLICT);
    }
}
