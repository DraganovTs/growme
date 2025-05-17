package com.home.mail.exception;

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
public class EmailExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(EmailProcessingException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailProcessingException(EmailProcessingException ex , WebRequest request){
        log.warn("Email receive error: {}", ex.getMessage());
        return buildErrorResponse(ex,request, HttpStatus.INTERNAL_SERVER_ERROR,"EMAIL_RECEIVE_ERROR");
    }
}
