package com.home.service.impl;

import com.home.growme.common.module.events.PaymentFailureEvent;
import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.exceptions.eventPublishing.EventPublishingException;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.service.EventPublisherService;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_FAILURES_TOPIC;
import static com.home.growme.common.module.config.kafka.topic.KafkaTopics.PAYMENT_INTENT_RESPONSES_TOPIC;

@Slf4j
@Service
public class EventPublisherServiceImpl implements EventPublisherService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisherServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPaymentIntentResponse(PaymentIntentResponseEvent response) {
        try {
            kafkaTemplate.send(PAYMENT_INTENT_RESPONSES_TOPIC, response)
                    .thenAccept(result -> log.debug("Published payment response for {}", response.getPaymentIntentId()))
                    .exceptionally(ex -> {
                        log.error("Failed to publish payment response", ex);
                        throw new EventPublishingException("Failed to publish payment response", ex);
                    });
        } catch (Exception e) {
            log.error("Critical publish failure", e);
            throw new EventPublishingException("Critical publishing failure", e);
        }
    }

    @Override
    public void publishPaymentFailure(PaymentIntentRequestEvent request, StripeException stripeException) {
        try {
            PaymentFailureEvent failureEvent = new PaymentFailureEvent(
                    request.getCorrelationId(),
                    "PAYMENT_ERROR",
                    stripeException.getMessage(),
                    request.getBasketId()
            );

            kafkaTemplate.send(PAYMENT_FAILURES_TOPIC,failureEvent)
                    .thenAccept(result -> {
                        log.debug( "Published payment failure for basket {} [Correlation: {}]",
                                request.getBasketId(),
                                request.getCorrelationId());
                    })
                    .exceptionally( ex -> {
                        log.error("Failed to publish payment failure event", ex);
                        return null;
                    });
        }catch (Exception e){
            log.error("Critical failure publishing payment failure event", e);
        }
    }
}
