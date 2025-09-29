package com.home.growme.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * DTO used when creating or updating a product.
 */
@Schema(name = "ProductRequest", description = "Payload to create or update a product")
@Data
@Builder
public class ProductRequestDTO {
    @Schema(description = "Name of the product", example = "Organic Apple", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String name;

    @Schema(description = "Brand of the product", example = "NatureFresh")
    @Size(max = 50, message = "Brand must be less than 50 characters")
    private String brand;

    @Schema(description = "Detailed product description", example = "Fresh organic apples grown without pesticides", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Price of the product", example = "3.49", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Schema(description = "Available stock units", example = "100")
    @Min(value = 0, message = "Units in stock must be a non-negative value")
    private int unitsInStock;

    @Schema(description = "Image URL of the product", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Name of the category to which product belongs", example = "Fruits", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Category name is required")
    private String categoryName;

    @Schema(description = "UUID of the owner of the product", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Owner ID is required")
    private UUID ownerId;
}
