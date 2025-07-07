package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class TaskStatusNotFoundException extends BaseException {
    public TaskStatusNotFoundException(String message) {
        super(message, "STATUS_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
