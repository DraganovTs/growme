package com.home.growme.common.module.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentIntentRequest {
    private String basketId;
    private BigDecimal amount;
    private String currency;
    private UUID userId;
}
