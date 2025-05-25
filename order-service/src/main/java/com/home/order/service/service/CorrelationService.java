package com.home.order.service.service;


import com.home.growme.common.module.events.PaymentIntentResponseEvent;

import java.util.concurrent.CompletableFuture;

/**
 * CorrelationService is responsible for managing correlation IDs used to track requests and responses
 * in the context of payment operations. It provides a mechanism to create correlation IDs,
 * map them to baskets, handle asynchronous responses, and retrieve payment intent information.
 */
public interface CorrelationService {

    public String createCorrelation(String basketId);
    public CompletableFuture<PaymentIntentResponseEvent> awaitResponse(String correlationId);
    public void completeResponse(String correlationId, PaymentIntentResponseEvent response);
    public void completeExceptionally(String correlationId, Throwable ex);
    String getPaymentIntentId(String basketId);
}
