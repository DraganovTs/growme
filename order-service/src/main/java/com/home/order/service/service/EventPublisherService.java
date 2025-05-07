package com.home.order.service.service;

import com.home.growme.common.module.dto.PaymentIntentRequest;

import java.math.BigDecimal;

public interface EventPublisherService {

   void sendCreatePaymentIntent(String basketId, BigDecimal amount);
}
