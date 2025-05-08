package com.home.service;

import com.home.growme.common.module.dto.PaymentIntentResponse;

public interface EventPublisherService {

    void  publishPaymentIntentResponse (PaymentIntentResponse response);
}
