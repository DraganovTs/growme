package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Schema(name = "OrderResponseDTO", description = "Response data returned after order retrieval")
@Data
@Builder
public class OrderResponseDTO {
    @Schema(description = "Unique order identifier", example = "order123")
    private String orderId;
    @Schema(description = "Date and time when order was placed")
    private Instant orderDate;
    @Schema(description = "Short name of the delivery method", example = "Express")
    private String deliveryMethodShortName;
    @Schema(description = "Total order cost including shipping", example = "55.99")
    private double total;
    @Schema(description = "Current status of the order", example = "PENDING")
    private String status;
    @Schema(description = "Subtotal price before shipping", example = "50.00")
    private double subTotal;
    @Schema(description = "Shipping cost", example = "5.99")
    private double shippingPrice;
    @Schema(description = "List of items included in the order")
    private List<OrderItemDTO> orderItems;

}
