package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaymentFailureEvent extends Event {
    private final String correlationId;
    private final String errorCode;
    private final String errorMessage;
    private Map<String, Object> additionalDetails;


    @JsonCreator
    public PaymentFailureEvent(
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("errorCode") String errorCode,
            @JsonProperty("errorMessage") String errorMessage) {
        this.correlationId = correlationId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
