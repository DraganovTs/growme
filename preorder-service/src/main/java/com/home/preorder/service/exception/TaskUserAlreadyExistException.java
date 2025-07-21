package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class TaskUserAlreadyExistException extends BaseException {
    public TaskUserAlreadyExistException(String message) {
        super(message, "TASK_USER_ALREADY_EXIST", HttpStatus.CONFLICT);
    }
}
