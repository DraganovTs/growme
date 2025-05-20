package com.home.order.service.service;


import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;


public interface EventPublisherService {

   CompletableFuture<PaymentIntentResponseEvent> sendCreatePaymentIntent(String basketId, BigDecimal amount);

   CompletableFuture<PaymentIntentResponseEvent> sendUpdatePaymentIntent(String basketId, BigDecimal amount);

    void publishCompletedOrder(OrderCompletedEvent event);
}
