package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.PaymentIntentResponse;
import com.home.order.service.service.EventHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_INTENT_RESPONSE;

@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {




    @Override
    @KafkaListener(topics = PAYMENT_INTENT_RESPONSE)
    public void  handlePaymentIntentResponse(PaymentIntentResponse response) {
        log.info("handlePaymentIntentRequest: {}", response);
    }
}
