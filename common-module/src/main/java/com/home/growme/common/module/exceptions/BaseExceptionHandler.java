package com.home.growme.common.module.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public abstract class BaseExceptionHandler {


    protected ResponseEntity<ErrorResponseDTO> buildErrorResponse(WebRequest request,
                                                                  HttpStatus status,
                                                                  String errorCode,
                                                                  String message) {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .apiPath(request.getDescription(false))
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, status);
    }

    protected ResponseEntity<ErrorResponseDTO> buildErrorResponse(Exception exception,
                                                                  WebRequest request,
                                                                  HttpStatus status,
                                                                  String errorCode) {
        return buildErrorResponse(request, status, errorCode, exception.getMessage());
    }

}
