package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.PaymentFailureEvent;
import com.home.growme.common.module.dto.PaymentIntentResponse;
import com.home.order.service.exception.PaymentFailedException;
import com.home.order.service.service.CorrelationService;
import com.home.order.service.service.EventHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_FAILURE_EVENTS;
import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_INTENT_RESPONSE;

@Slf4j
@Service
public class EventHandlerServiceImpl implements EventHandlerService {

    private final CorrelationService correlationService;

    public EventHandlerServiceImpl(CorrelationService correlationService) {
        this.correlationService = correlationService;
    }


    @Override
    @KafkaListener(topics = PAYMENT_INTENT_RESPONSE)
    public void handlePaymentIntentResponse(PaymentIntentResponse response) {
        log.info("Processing payment intent response: {}", response);
        correlationService.completeResponse(response.getCorrelationId(), response);
    }

    @Override
    @KafkaListener(topics = PAYMENT_FAILURE_EVENTS)
    public void handlePaymentFailure(PaymentFailureEvent event) {
        log.warn("Processing payment failure: {}", event);
        correlationService.completeExceptionally(
                event.getCorrelationId(),
                new PaymentFailedException(event.getErrorMessage()));
    }
}
