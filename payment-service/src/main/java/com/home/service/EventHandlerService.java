package com.home.service;

import com.home.growme.common.module.events.PaymentIntentRequestEvent;

public interface EventHandlerService {

    void handleEvent(PaymentIntentRequestEvent request);
}
