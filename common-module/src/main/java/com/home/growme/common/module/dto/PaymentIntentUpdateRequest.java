package com.home.growme.common.module.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentIntentUpdateRequest {
    private String basketId;
    private BigDecimal amount;
    private String currency;
    private UUID userId;
    private String correlationId;
    private String intentId;
}
