package com.home.growme.common.module.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentIntentResponse {
    private String clientSecret;
    private String paymentIntentId;
    private String status;
    private String correlationId;
    private BigDecimal amount;
    private String currency;
}
