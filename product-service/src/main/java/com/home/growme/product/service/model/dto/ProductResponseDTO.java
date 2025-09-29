package com.home.growme.product.service.model.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO used for returning product details to the client.
 */
@Schema(name = "ProductResponse", description = "Represents a product returned in API response")
@Data
@Builder
public class ProductResponseDTO {
    @Schema(description = "Product ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", format = "uuid")
    private UUID productId;
    @Schema(description = "Product name", example = "Organic Apple")
    private String name;
    @Schema(description = "Brand of the product", example = "NatureFresh")
    private String brand;
    @Schema(description = "Detailed description of the product", example = "Fresh organic apples grown without pesticides")
    private String description;
    @Schema(description = "Price of the product", example = "3.49")
    private BigDecimal price;
    @Schema(description = "Number of units in stock", example = "100")
    private int unitsInStock;
    @Schema(description = "URL of the product image", example = "https://example.com/image.jpg")
    private String imageUrl;
    @Schema(description = "Category name", example = "Fruits")
    private String categoryName;
    @Schema(description = "Owner name", example = "John Doe")
    private String ownerName;
    @Schema(description = "Owner ID", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", format = "uuid")
    private UUID ownerId;
    @Schema(description = "ID of the category", example = "550e8400-e29b-41d4-a716-446655440000", format = "uuid")
    private UUID productCategoryId;
}
