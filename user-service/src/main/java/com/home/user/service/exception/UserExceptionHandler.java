package com.home.user.service.exception;

import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
@Slf4j
@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataConflict(DataConflictException ex, WebRequest request) {
        log.warn("Data conflict: {}", ex.getMessage());
        return buildErrorResponse(request, HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(NotValidUserRoleException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRole(NotValidUserRoleException ex, WebRequest request) {
        log.warn("Invalid role: {}", ex.getMessage());
        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, "INVALID_ROLE", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(request, HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserExists(UserAlreadyExistException ex, WebRequest request) {
        log.warn("User exists: {}", ex.getMessage());
        return buildErrorResponse(request, HttpStatus.CONFLICT, "USER_EXISTS", ex.getMessage());
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class, EmailAlreadyExistsException.class})
    public ResponseEntity<ErrorResponseDTO> handleCredentialConflicts(RuntimeException ex, WebRequest request) {
        String errorCode = ex instanceof UsernameAlreadyExistsException ? "USERNAME_EXISTS" : "EMAIL_EXISTS";
        log.warn("Credential conflict: {}", ex.getMessage());
        return buildErrorResponse(request, HttpStatus.CONFLICT, errorCode, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error", ex);
        return buildErrorResponse(request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(WebRequest request,
                                                                HttpStatus status,
                                                                String errorCode,
                                                                String message) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .apiPath(request.getDescription(false))
                .errorCode(status)
                .errorMessage(message)
                .errorTime(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, status);
    }
}
