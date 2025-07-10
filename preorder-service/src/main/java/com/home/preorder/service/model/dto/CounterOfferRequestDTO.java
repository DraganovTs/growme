package com.home.preorder.service.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CounterOfferRequestDTO(
        @Positive @DecimalMin("0.01")BigDecimal counterPrice,
        @NotBlank @Size(max = 500) String message
        ) {
}
