package com.home.service.impl;

import com.home.growme.common.module.dto.PaymentIntentRequest;
import com.home.growme.common.module.dto.PaymentIntentResponse;
import com.home.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {


    @Override
    public PaymentIntentResponse createOrUpdatePaymentIntent(PaymentIntentRequest request) throws StripeException {


        Stripe.apiKey = System.getenv("StripePrivateKey");
        PaymentIntent intent;
        List<String> paymentTypes = List.of("card");

        if (request.isCreateOperation()) {
            intent = createNewPaymentIntent(request, paymentTypes);
        } else {
            intent = updateExistingPaymentIntent(request);
        }

        return buildPaymentResponse(intent);

    }

    private PaymentIntentResponse buildPaymentResponse(PaymentIntent intent) {
        return PaymentIntentResponse.builder()
                .paymentIntentId(intent.getId())
                .clientSecret(intent.getClientSecret())
                .status(intent.getStatus())
                .amount(BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100)))
                .currency(intent.getCurrency())
                .build();
    }
    private PaymentIntent createNewPaymentIntent(PaymentIntentRequest request, List<String> paymentTypes) throws StripeException {

            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency("usd")
                    .addAllPaymentMethodType(paymentTypes)
                    .putMetadata("basketId", request.getBasketId())
                    .build();

            return PaymentIntent.create(createParams);
    }

    private PaymentIntent updateExistingPaymentIntent(PaymentIntentRequest request) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(request.getPaymentIntentId());

        PaymentIntentUpdateParams updateParams = PaymentIntentUpdateParams.builder()
                .setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                .build();

        return intent.update(updateParams);
    }

}
