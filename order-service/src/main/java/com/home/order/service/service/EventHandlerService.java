package com.home.order.service.service;

import com.home.growme.common.module.dto.PaymentFailureEvent;
import com.home.growme.common.module.dto.PaymentIntentResponse;

public interface EventHandlerService {

    void handlePaymentIntentResponse(PaymentIntentResponse response);

    void handlePaymentFailure(PaymentFailureEvent event);
}
