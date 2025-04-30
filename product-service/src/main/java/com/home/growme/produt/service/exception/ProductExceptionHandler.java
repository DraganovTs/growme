package com.home.growme.produt.service.exception;

import com.home.growme.common.module.exceptions.BaseExceptionHandler;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@ControllerAdvice
public class ProductExceptionHandler extends BaseExceptionHandler {


    @ExceptionHandler({
            ProductNotFoundException.class,
            CategoryNotFoundException.class,
            OwnerNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFoundExceptions(RuntimeException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileStorageException(FileStorageException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.INSUFFICIENT_STORAGE, "FILE_STORAGE_ERROR");
    }

    @ExceptionHandler(ProductRequestNotValidNameException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidName(ProductRequestNotValidNameException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_PRODUCT_NAME");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSize(MaxUploadSizeExceededException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE");
    }

    @ExceptionHandler(OwnerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleOwnerExists(OwnerAlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT, "OWNER_EXISTS");
    }
}