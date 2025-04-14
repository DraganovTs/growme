package com.home.growme.produt.service.exception;

import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@ControllerAdvice
public class ProductExceptionHandler {


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFoundException(ProductNotFoundException exception, WebRequest webRequest) {
        return buildErrorResponse(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileStorageException(FileStorageException exception, WebRequest webRequest) {
        return buildErrorResponse(exception, webRequest, HttpStatus.INSUFFICIENT_STORAGE);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFoundException(CategoryNotFoundException exception, WebRequest webRequest) {
        return buildErrorResponse(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFoundException(OwnerNotFoundException exception, WebRequest webRequest) {
        return buildErrorResponse(exception, webRequest, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductRequestNotValidNameException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFoundException(ProductRequestNotValidNameException exception, WebRequest webRequest) {
        return buildErrorResponse(exception, webRequest, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeException(MaxUploadSizeExceededException exception, WebRequest webRequest) {
        return buildErrorResponse(exception, webRequest, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(Exception exception, WebRequest webRequest, HttpStatus httpStatus) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                webRequest.getDescription(false),
                httpStatus,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }


}
