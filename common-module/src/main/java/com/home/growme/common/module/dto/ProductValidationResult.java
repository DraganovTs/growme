package com.home.growme.common.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ProductValidationResult {

    private UUID productId;
    private boolean valid;
    private String reason;
}
