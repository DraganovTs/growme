package com.home.service;

import com.home.model.PaymentIntentRequest;
import com.home.model.PaymentIntentResponse;

public interface PaymentService {
    PaymentIntentResponse createOrUpdatePaymentIntent(PaymentIntentRequest request);

    void handlePaymentSucceeded(String paymentIntentId);

    void handlePaymentFailed(String paymentIntentId);

}
