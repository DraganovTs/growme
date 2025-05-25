package com.home.order.service.model.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BasketItem {
    @NotNull(message = "Product ID is required")
    private UUID productId;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    @NotNull(message = "Product name is required")
    @Size(min = 1, max = 30, message = "Product name must be between 1 and 30 characters")
    private String name;
    @Min(value = 0, message = "Units in stock cannot be negative")
    private int unitsInStock;
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;
    @NotNull(message = "Brand name is required")
    @Size(min = 1, max = 30, message = "Brand name must be between 1 and 30 characters")
    private String brandName;
    @NotNull(message = "Category name is required")
    @Size(min = 1, max = 30, message = "Category name must be between 1 and 30 characters")
    private String categoryName;

}
