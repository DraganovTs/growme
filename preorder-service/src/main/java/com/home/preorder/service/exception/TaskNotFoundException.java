package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends BaseException {
    public TaskNotFoundException(String message) {
        super(message, "TASK_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
