package com.home.order.service.model.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BasketItem {
    @NotNull
    private UUID productId;
    private int quantity;
    @NotNull
    private String name;
    @Min(value = 0)
    private int unitsInStock;
    private String imageUrl;
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal price;
    @NotNull
    private String brandName;
    @NotNull
    private String categoryName;

}
