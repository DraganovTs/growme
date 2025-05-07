package com.home.service;

import com.home.growme.common.module.dto.PaymentIntentRequest;

public interface EventHandlerService {

    void handleEvent(PaymentIntentRequest request);
}
