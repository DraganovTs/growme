package com.home.order.service.service;


import java.math.BigDecimal;


public interface EventPublisherService {

   void sendCreatePaymentIntent(String basketId, BigDecimal amount);

   void sendUpdatePaymentIntent(String basketId, BigDecimal amount);
}
