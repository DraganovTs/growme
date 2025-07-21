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
            CategoryNotFoundException.class,
            TaskNotFoundException.class,
            BidNotFoundException.class,
            TaskUserNotFoundException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException ex, WebRequest request){
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    @ExceptionHandler(InvalidBidStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidBidStatusException(
            InvalidBidStatusException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_BID_STATUS");
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedActionException(
            UnauthorizedActionException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACTION");
    }

    @ExceptionHandler(BidExpiredException.class)
    public ResponseEntity<ErrorResponseDTO> handleBidExpiredException(
            BidExpiredException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.GONE, "BID_EXPIRED");
    }

    @ExceptionHandler(DuplicateBidException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateBidException(
            DuplicateBidException ex, WebRequest request) {
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT, "DUPLICATE_BID");
    }
}
