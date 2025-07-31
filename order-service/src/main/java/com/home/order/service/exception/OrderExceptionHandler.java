package com.home.order.service.exception;

import com.home.growme.common.module.exceptions.BaseExceptionHandler;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@ControllerAdvice
public class OrderExceptionHandler extends BaseExceptionHandler {


    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponseDTO> handlePaymentProcessing(PaymentProcessingException ex, WebRequest request) {
        log.warn("Payment is not valid: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT_PROCESSING_ERROR");
    }

    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidProduct(InvalidProductException ex, WebRequest request) {
        log.warn("Product is not valid: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST, "INVALID_PRODUCT");
    }

    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBasketNotFound(BasketNotFoundException ex, WebRequest request) {
        log.warn("Basket not found: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "BASKET_NOT_FOUND");
    }


    @ExceptionHandler(DeliveryMethodNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleDeliveryMethodNotFound(DeliveryMethodNotFoundException ex, WebRequest request) {
        log.warn("Delivery Method not found: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "DELIVERY_METHOD_NOT_FOUND");
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleOwnerNotFound(OwnerNotFoundException ex, WebRequest request) {
        log.warn("Owner not found: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "OWNER_NOT_FOUND");
    }

    @ExceptionHandler(OrderNotfoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleOwnerNotFound(OrderNotfoundException ex, WebRequest request) {
        log.warn("Order not found: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND");
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error in order service", ex);
        return buildErrorResponse(request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ORDER_SERVICE_ERROR",
                "An unexpected error occurred in order service");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        String message = "Validation failed for one or more fields";
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .apiPath(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST)
                .errorCode("VALIDATION_ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }
}
