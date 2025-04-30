package com.home.order.service.exception;

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
public class OrderExceptionHandler  {



    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(BasketNotFoundException ex, WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(request, HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage());
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
