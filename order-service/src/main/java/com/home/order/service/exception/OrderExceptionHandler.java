package com.home.order.service.exception;

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
public class OrderExceptionHandler extends BaseExceptionHandler {


    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponseDTO> handleBasketNotFound(PaymentProcessingException ex, WebRequest request) {
        log.warn("Payment is not valid: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_PROCESSING_ERROR");
    }

    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<ErrorResponseDTO> handleBasketNotFound(InvalidProductException ex, WebRequest request) {
        log.warn("Product is not valid: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_PRODUCT");
    }

    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBasketNotFound(BasketNotFoundException ex, WebRequest request) {
        log.warn("Basket not found: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "BASKET_NOT_FOUND");
    }


    @ExceptionHandler(DeliveryMethodNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBasketNotFound(DeliveryMethodNotFoundException ex, WebRequest request) {
        log.warn("Delivery Method not found: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "DELIVERY_METHOD_NOT_FOUND");
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error in order service", ex);
        return buildErrorResponse(request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ORDER_SERVICE_ERROR",
                "An unexpected error occurred in order service");
    }
}
