package com.home.service;

import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.stripe.exception.StripeException;

public interface EventPublisherService {

    void  publishPaymentIntentResponse (PaymentIntentResponseEvent response);

    void publishPaymentFailure(PaymentIntentRequestEvent request, StripeException e);
}
