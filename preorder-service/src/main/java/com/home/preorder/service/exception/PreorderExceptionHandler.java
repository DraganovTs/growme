package com.home.preorder.service.exception;

import com.home.growme.common.module.exceptions.BaseExceptionHandler;
import com.home.growme.common.module.exceptions.CategoryNotFoundException;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class PreorderExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler({
            CategoryNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException ex, WebRequest request){
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }
}
