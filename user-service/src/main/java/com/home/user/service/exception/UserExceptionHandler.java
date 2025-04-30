package com.home.user.service.exception;

import com.home.growme.common.module.exceptions.BaseExceptionHandler;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class UserExceptionHandler  extends BaseExceptionHandler {

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataConflict(DataConflictException ex, WebRequest request) {
        log.warn("Data conflict: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT, ex.getErrorCode());
    }

    @ExceptionHandler(NotValidUserRoleException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRole(NotValidUserRoleException ex, WebRequest request) {
        log.warn("Invalid role: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_ROLE");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    @ExceptionHandler({
            UserAlreadyExistException.class,
            UsernameAlreadyExistsException.class,
            EmailAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleUserExists(RuntimeException ex, WebRequest request) {
        String errorCode = ex instanceof UsernameAlreadyExistsException ? "USERNAME_EXISTS" :
                ex instanceof EmailAlreadyExistsException ? "EMAIL_EXISTS" : "USER_EXISTS";
        log.warn("User exists: {}", ex.getMessage());
        return buildErrorResponse(request, HttpStatus.CONFLICT, errorCode, ex.getMessage());
    }
}
