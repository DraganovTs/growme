package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Schema(name = "OrderItemDTO", description = "Details of a single item in an order")
@Data
@Builder
public class OrderItemDTO {
    @Schema(description = "Product identifier", example = "prod123")
    @NotBlank(message = "Product ID cannot be blank")
    private String productId;
    @Schema(description = "Name of the product", example = "Apple")
    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    private String productName;
    @Schema(description = "URL of the product image", example = "http://example.com/image.jpg")
    @NotBlank(message = "Image URL cannot be blank")
    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
    private String imageUrl;
    @Schema(description = "Unit price of the product", example = "1.99")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private double unitPrice;
    @Schema(description = "Quantity ordered", example = "3")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

}
