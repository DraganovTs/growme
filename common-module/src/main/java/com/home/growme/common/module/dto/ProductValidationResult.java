package com.home.growme.common.module.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ProductValidationResult {

    @NotNull(message = "Product ID cannot be null")
    private UUID productId;

    @NotNull(message = "Validation status cannot be null")
    private Boolean valid;

    private String reason;
}
