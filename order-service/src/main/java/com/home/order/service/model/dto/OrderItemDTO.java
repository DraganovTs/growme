package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(name = "OrderItemDTO", description = "Details of a single item in an order")
@Data
@Builder
public class OrderItemDTO {
    @Schema(description = "Product identifier", example = "prod123")
    private String productId;
    @Schema(description = "Name of the product", example = "Apple")
    private String productName;
    @Schema(description = "URL of the product image", example = "http://example.com/image.jpg")
    private String imageUrl;
    @Schema(description = "Unit price of the product", example = "1.99")
    private double unitPrice;
    @Schema(description = "Quantity ordered", example = "3")
    private Integer quantity;

}
