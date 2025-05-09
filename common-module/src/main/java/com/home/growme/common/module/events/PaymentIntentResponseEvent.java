package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaymentIntentResponseEvent extends Event {
    private final String clientSecret;
    private final String paymentIntentId;
    private final String status;
    private final String correlationId;



    @JsonCreator
    public PaymentIntentResponseEvent(
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("paymentIntentId") String paymentIntentId,
            @JsonProperty("clientSecret") String clientSecret,
            @JsonProperty("status") String status) {
        this.correlationId = correlationId;
        this.paymentIntentId = paymentIntentId;
        this.clientSecret = clientSecret;
        this.status = status;
    }
}
