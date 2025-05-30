package com.home.service.impl;

import com.home.growme.common.module.events.PaymentIntentRequestEvent;
import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.service.EventPublisherService;
import com.home.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${STRIPE_API_KEY}")
    private String STRIPE_API_KEY;
    private final EventPublisherService eventPublisherService;

    public PaymentServiceImpl(EventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }


    @Override
    public PaymentIntentResponseEvent createOrUpdatePaymentIntent(PaymentIntentRequestEvent request) throws StripeException {
        Stripe.apiKey = STRIPE_API_KEY;


        log.debug("Processing payment intent request: {}", request);

        try {
            List<String> paymentTypes = List.of("card");
            PaymentIntent intent = request.isCreateOperation()
                    ? createNewPaymentIntent(request, paymentTypes)
                    : updateExistingPaymentIntent(request);

            return buildPaymentResponse(intent, request.getCorrelationId());

        } catch (StripeException e) {
            log.error("Stripe operation failed for request: {}", request, e);
            eventPublisherService.publishPaymentFailure(request,e);
            throw e;
        }

    }

    private PaymentIntentResponseEvent buildPaymentResponse(PaymentIntent intent, String correlationId) {
        return new PaymentIntentResponseEvent(
                correlationId,
                intent.getId(),
                intent.getClientSecret(),
                intent.getStatus()
        );
    }


    private PaymentIntent createNewPaymentIntent(PaymentIntentRequestEvent request, List<String> paymentTypes)
            throws StripeException {

        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(convertToCents(request.getAmount()))
                .setCurrency("usd")
                .addAllPaymentMethodType(paymentTypes)
                .putMetadata("correlationId", request.getCorrelationId())
                .putMetadata("basketId", request.getBasketId())
                .build();

        log.debug("Creating new payment intent for basket: {}", request.getBasketId());
        return PaymentIntent.create(createParams);
    }

    private PaymentIntent updateExistingPaymentIntent(PaymentIntentRequestEvent request) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(request.getPaymentIntentId());

        PaymentIntentUpdateParams updateParams = PaymentIntentUpdateParams.builder()
                .setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                .build();

        return intent.update(updateParams);
    }

    private long convertToCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }

}
