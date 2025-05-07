package com.home.growme.common.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class BasketItemDTO {

    private UUID productId;
    private int quantity;
    private BigDecimal price;
}
