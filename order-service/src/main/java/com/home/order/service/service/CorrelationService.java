package com.home.order.service.service;


import com.home.growme.common.module.dto.PaymentIntentResponse;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CorrelationService {

    public String createCorrelation(String basketId);
    public CompletableFuture<PaymentIntentResponse> awaitResponse(String correlationId);
    public void completeResponse(String correlationId, PaymentIntentResponse response);
    public void completeExceptionally(String correlationId, Throwable ex);
//    public Optional<String> getCorrelationId(String basketId);
}
