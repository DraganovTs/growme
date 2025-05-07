package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.PaymentIntentRequest;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.order.service.service.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String PAYMENT_INTENT = "payment.intent";

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void sendCreatePaymentIntent(String basketId, BigDecimal amount) {
        PaymentIntentRequest request = new PaymentIntentRequest();
        request.setBasketId(basketId);
        request.setAmount(amount);

        try {
            kafkaTemplate.send(PAYMENT_INTENT,request)
                    .thenAccept(result -> {
                        log.debug("Publishing payment intent for basketId: {} successful", basketId);
                        //TODO: Add metrics
                    })
                    .exceptionally( ex-> {
                        log.error("Publish failed for request: {}", request, ex);
                        //TODO: Add metrics
                        throw new EventPublishingException("Failed to publish paymentIntent assignment");
                    });
        }catch (Exception e){
            log.error("Critical publish failure for basketId: {}", basketId, e);
            //TODO: Add dead letter queue handling
            throw new EventPublishingException("Critical publishing failure");
        }

    }
}
