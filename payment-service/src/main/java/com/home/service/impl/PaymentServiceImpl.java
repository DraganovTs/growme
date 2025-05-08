package com.home.service.impl;

import com.home.growme.common.module.dto.PaymentIntentRequest;
import com.home.growme.common.module.dto.PaymentIntentResponse;
import com.home.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {


    @Override
    public PaymentIntentResponse createOrUpdatePaymentIntent(PaymentIntentRequest request) throws StripeException {


        Stripe.apiKey = System.getenv("StripePrivateKey");
        PaymentIntent intent;
        List<String> paymentTypes = new ArrayList<>();
        paymentTypes.add("card");
        PaymentIntentCreateParams createParams =
                PaymentIntentCreateParams.builder()
                        .setAmount(request.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                        .setCurrency("usd")
                        .addAllPaymentMethodType(paymentTypes)
                        .build();
        intent = PaymentIntent.create(createParams);

        PaymentIntentResponse response = new PaymentIntentResponse();
        response.setClientSecret(intent.getClientSecret());
        response.setPaymentIntentId(intent.getId());
        response.setStatus(intent.getStatus());

        return response;
    }

    @Override
    public void handlePaymentSucceeded(String paymentIntentId) {

    }

    @Override
    public void handlePaymentFailed(String paymentIntentId) {

    }
}
