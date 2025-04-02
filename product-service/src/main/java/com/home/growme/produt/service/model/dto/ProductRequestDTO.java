package com.home.growme.produt.service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductRequestDTO {
    @NotBlank(message = "Product name is required")
    private String name;

    private String brand;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be a positive value")
    private BigDecimal price;

    @Min(value = 0, message = "Units in stock must be a non-negative value")
    private int unitsInStock;

    private String imageUrl;

    @NotNull(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;
}
