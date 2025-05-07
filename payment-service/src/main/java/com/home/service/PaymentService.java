package com.home.service;

import com.home.growme.common.module.dto.PaymentIntentRequest;
import com.home.model.PaymentIntentResponse;
import com.stripe.exception.StripeException;

public interface PaymentService {
    PaymentIntentResponse createOrUpdatePaymentIntent(PaymentIntentRequest request) throws StripeException;

    void handlePaymentSucceeded(String paymentIntentId);

    void handlePaymentFailed(String paymentIntentId);

}
