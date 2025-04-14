package com.home.growme.produt.service.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductRequestDTO {
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String name;

    @Size(max = 50, message = "Brand must be less than 50 characters")
    private String brand;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0, message = "Units in stock must be a non-negative value")
    private int unitsInStock;

    private String imageUrl;

    @NotNull(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;
}
