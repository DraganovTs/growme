package com.home.order.service.service;


import com.home.growme.common.module.dto.PaymentIntentResponse;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;


public interface EventPublisherService {

   public CompletableFuture<PaymentIntentResponse> sendCreatePaymentIntent(String basketId, BigDecimal amount);

   public CompletableFuture<PaymentIntentResponse> sendUpdatePaymentIntent(String basketId, BigDecimal amount);

   public CompletableFuture<Void> publishPaymentIntentResponse(PaymentIntentResponse response);
}
