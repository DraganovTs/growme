package com.home.growme.common.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailureEvent {
    private String correlationId;
    private String basketId;
    private String paymentIntentId;
    private String errorCode;
    private String errorMessage;
    private Instant timestamp;
    private Map<String, Object> additionalDetails;
}
