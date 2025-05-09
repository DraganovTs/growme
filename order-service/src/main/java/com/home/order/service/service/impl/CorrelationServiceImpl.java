package com.home.order.service.service.impl;

import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.service.CorrelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorrelationServiceImpl implements CorrelationService {

    private final Map<String, CompletableFuture<PaymentIntentResponseEvent>> pendingRequests = new ConcurrentHashMap<>();
    private final Map<String, String> basketToCorrelationMap = new ConcurrentHashMap<>();
    private final Map<String, String> basketToPaymentIntentMap = new ConcurrentHashMap<>(); // New mapping
    private final BasketRepository basketRepository;



    @Override
    public String createCorrelation(String basketId) {
        if (basketId == null || basketId.isBlank()) {
            throw new IllegalArgumentException("Basket ID cannot be null or empty");
        }

        String correlationId = UUID.randomUUID().toString();
        basketToCorrelationMap.put(basketId, correlationId);
        log.debug("Created correlation ID {} for basket {}", correlationId, basketId);
        return correlationId;
    }

    @Override
    public CompletableFuture<PaymentIntentResponseEvent> awaitResponse(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("Correlation ID cannot be null or empty");
        }

        CompletableFuture<PaymentIntentResponseEvent> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        future.whenComplete((r, ex) -> {
            pendingRequests.remove(correlationId);
            log.debug("Completed future for correlation ID {}", correlationId);
        });

        return future;
    }

    @Override
    public void completeResponse(String correlationId, PaymentIntentResponseEvent response) {
        if (correlationId == null || correlationId.isBlank()) {
            log.error("Attempted to complete response with null/empty correlation ID");
            return;
        }

        CompletableFuture<PaymentIntentResponseEvent> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(response);

            // Store payment intent ID when response is received
            if (response.getPaymentIntentId() != null) {
                String basketId = getBasketIdByCorrelation(correlationId);
                if (basketId != null) {
                    basketToPaymentIntentMap.put(basketId, response.getPaymentIntentId());
                    updateBasketPaymentIntent(basketId, response.getPaymentIntentId()); // Persist to DB
                }
            }

            basketToCorrelationMap.values().remove(correlationId);
            log.debug("Successfully completed response for correlation ID {}", correlationId);
        } else {
            log.warn("No pending request found for correlation ID {}", correlationId);
        }
    }

    @Override
    public String getPaymentIntentId(String basketId) {
        if (basketId == null || basketId.isBlank()) {
            throw new IllegalArgumentException("Basket ID cannot be null or empty");
        }

        // First try in-memory cache
        String paymentIntentId = basketToPaymentIntentMap.get(basketId);
        if (paymentIntentId != null) {
            return paymentIntentId;
        }

        return basketRepository.findById(basketId)
                .map(Basket::getPaymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("No payment intent found for basket: " + basketId));
    }

    @Override
    public void completeExceptionally(String correlationId, Throwable ex) {
        if (correlationId == null || correlationId.isBlank()) {
            log.error("Attempted to complete exceptionally with null/empty correlation ID");
            return;
        }

        CompletableFuture<PaymentIntentResponseEvent> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.completeExceptionally(ex);
            basketToCorrelationMap.values().remove(correlationId);
            log.warn("Completed exceptionally correlation ID {} with error: {}", correlationId, ex.getMessage());
        } else {
            log.warn("No pending request found for correlation ID {} to complete exceptionally", correlationId);
        }
    }

    private String getBasketIdByCorrelation(String correlationId) {
        return basketToCorrelationMap.entrySet().stream()
                .filter(entry -> correlationId.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private void updateBasketPaymentIntent(String basketId, String paymentIntentId) {
        try {
            Basket basket = basketRepository.findById(basketId)
                    .orElseThrow(() -> new BasketNotFoundException(basketId));
            basket.setPaymentIntentId(paymentIntentId);
            basketRepository.save(basket);
        } catch (Exception e) {
            log.error("Failed to update payment intent ID for basket: {}", basketId, e);
        }
    }
}