package com.home.order.service.exception;

public class BasketNotFoundException extends RuntimeException{
    public BasketNotFoundException(String message) {
        super(message);
    }
}
