package com.home.mail.service.exception;

import com.home.growme.common.module.exceptions.BaseExceptionHandler;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import com.home.mail.service.service.impl.EmailDeletionException;
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


    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailSendingException(EmailSendingException ex , WebRequest request){
        log.warn("Email sending error: {}", ex.getMessage());
        return buildErrorResponse(ex,request, HttpStatus.INTERNAL_SERVER_ERROR,"EMAIL_SENDING_ERROR");
    }

    @ExceptionHandler(EmailDeletionException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailDeletingException(EmailDeletionException ex , WebRequest request){
        log.warn("Email deleting error: {}", ex.getMessage());
        return buildErrorResponse(ex,request, HttpStatus.INTERNAL_SERVER_ERROR,"EMAIL_DELETING_ERROR");
    }


}
