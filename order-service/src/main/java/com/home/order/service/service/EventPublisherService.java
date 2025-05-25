package com.home.order.service.service;


import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;


/**
 * The EventPublisherService interface provides methods for publishing events related to payment intents and orders.
 * It defines operations to create or update payment intent events and publish order completion events.
 */
public interface EventPublisherService {

   CompletableFuture<PaymentIntentResponseEvent> sendCreatePaymentIntent(String basketId, BigDecimal amount);

   CompletableFuture<PaymentIntentResponseEvent> sendUpdatePaymentIntent(String basketId, BigDecimal amount);

    void publishCompletedOrder(OrderCompletedEvent event);
}
