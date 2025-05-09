package com.home.service;

import com.home.growme.common.module.events.PaymentIntentResponseEvent;

public interface EventPublisherService {

    void  publishPaymentIntentResponse (PaymentIntentResponseEvent response);
}
