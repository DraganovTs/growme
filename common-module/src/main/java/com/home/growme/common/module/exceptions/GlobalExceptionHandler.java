package com.home.growme.common.module.exceptions;

import com.home.growme.common.module.exceptions.data.ConstraintViolationException;
import com.home.growme.common.module.exceptions.data.DataValidationException;
import com.home.growme.common.module.exceptions.data.InvalidRequestException;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.growme.common.module.exceptions.rate.RateLimitExceededException;
import com.home.growme.common.module.exceptions.security.ForbiddenOperationException;
import com.home.growme.common.module.exceptions.security.TokenValidationException;
import com.home.growme.common.module.exceptions.security.UnauthorizedAccessException;
import com.home.growme.common.module.exceptions.system.CacheOperationException;
import com.home.growme.common.module.exceptions.system.DatabaseConnectionException;
import com.home.growme.common.module.exceptions.system.ExternalServiceIntegrationException;
import com.home.growme.common.module.exceptions.system.FileSystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler{

    // Event Publishing
    @ExceptionHandler(EventPublishingException.class)
    public ResponseEntity<ErrorResponseDTO> handleEventPublishingException(
            EventPublishingException ex, WebRequest request) {
        return buildErrorResponse(ex, request, ex.getHttpStatus(), ex.getErrorCode());
    }

    // System/Infrastructure Exceptions
    @ExceptionHandler({
            DatabaseConnectionException.class,
            ExternalServiceIntegrationException.class,
            CacheOperationException.class,
            FileSystemException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleSystemExceptions(
            BaseException ex, WebRequest request) {
        return buildErrorResponse(ex, request, ex.getHttpStatus(), ex.getErrorCode());
    }

    // Security/Auth Exceptions
    @ExceptionHandler({
            UnauthorizedAccessException.class,
            ForbiddenOperationException.class,
            TokenValidationException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleSecurityExceptions(
            BaseException ex, WebRequest request) {
        return buildErrorResponse(ex, request, ex.getHttpStatus(), ex.getErrorCode());
    }

    // Data Validation Exceptions
    @ExceptionHandler({
            InvalidRequestException.class,
            DataValidationException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            BaseException ex, WebRequest request) {
        return buildErrorResponse(ex, request, ex.getHttpStatus(), ex.getErrorCode());
    }

    // Rate Limiting/Throttling
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleRateLimitException(
            RateLimitExceededException ex, WebRequest request) {
        return buildErrorResponse(ex, request, ex.getHttpStatus(), ex.getErrorCode());
    }

    // Fallback for any unhandled BaseException
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDTO> handleBaseException(
            BaseException ex, WebRequest request) {
        return buildErrorResponse(ex, request, ex.getHttpStatus(), ex.getErrorCode());
    }
}