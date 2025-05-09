package com.home.order.service.service;


import com.home.growme.common.module.events.PaymentIntentResponseEvent;

import java.util.concurrent.CompletableFuture;

public interface CorrelationService {

    public String createCorrelation(String basketId);
    public CompletableFuture<PaymentIntentResponseEvent> awaitResponse(String correlationId);
    public void completeResponse(String correlationId, PaymentIntentResponseEvent response);
    public void completeExceptionally(String correlationId, Throwable ex);
    String getPaymentIntentId(String basketId);
}
