package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaymentIntentRequestEvent extends Event {
    private final String basketId;
    private final BigDecimal amount;
    private final String correlationId;
    private final boolean isCreateOperation;
    private final String paymentIntentId;


    @JsonCreator
    public PaymentIntentRequestEvent(
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("basketId") String basketId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("createOperation") boolean createOperation,
            @JsonProperty(value = "paymentIntentId", required = false) String paymentIntentId) {
        this.correlationId = correlationId;
        this.basketId = basketId;
        this.amount = amount;
        this.isCreateOperation = createOperation;
        this.paymentIntentId = paymentIntentId;
    }
}



