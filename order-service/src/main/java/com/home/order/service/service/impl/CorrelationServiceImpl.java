package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.PaymentIntentResponse;
import com.home.order.service.service.CorrelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CorrelationServiceImpl implements CorrelationService {

    private final Map<String,CompletableFuture<PaymentIntentResponse>> pendingRequests = new ConcurrentHashMap<>();
    private final Map<String,String> basketToCorrelationMap = new ConcurrentHashMap<>();



    @Override
    public String createCorrelation(String basketId) {
        String correlationId = UUID.randomUUID().toString();
        basketToCorrelationMap.put(basketId, correlationId);
        return correlationId;
    }

    @Override
    public CompletableFuture<PaymentIntentResponse> awaitResponse(String correlationId) {
        CompletableFuture<PaymentIntentResponse> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        future.whenComplete((r, ex) -> {
            pendingRequests.remove(correlationId);
        });

        return future;
    }

    @Override
    public void completeResponse(String correlationId, PaymentIntentResponse response) {
        CompletableFuture<PaymentIntentResponse> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(response);
            basketToCorrelationMap.values().remove(correlationId);
        }
    }

    public void completeExceptionally(String correlationId, Throwable ex) {
        CompletableFuture<PaymentIntentResponse> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.completeExceptionally(ex);
            basketToCorrelationMap.values().remove(correlationId);
        }
    }


}
