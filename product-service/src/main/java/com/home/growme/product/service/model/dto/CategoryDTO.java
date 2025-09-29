package com.home.growme.product.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
/**
 * Represents a product category in the system.
 * Categories are used to organize and group related products.
 */
@Schema(
        name = "ProductCategory",
        description = """
        Organizational unit for grouping related products.
        Used for navigation, filtering, and product discovery.
        """
)
@Data
@Builder
public class CategoryDTO {
    @Schema(
            description = "Unique identifier for the category",
            example = "550e8400-e29b-41d4-a716-446655440000",
            format = "uuid"
    )
    private UUID categoryId;
    @Schema(
            description = "Display name for the category",
            example = "Electronics",
            minLength = 3,
            maxLength = 10,
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "^[a-zA-Z0-9\\s]+$"
    )
    @Size(min = 3, max = 10)
    @NotBlank(message = "Category name is required")
    private String categoryName;
}
