package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Schema(name = "OrderResponseDTO", description = "Response data returned after order retrieval")
@Data
@Builder
public class OrderResponseDTO {
    @Schema(description = "Unique order identifier", example = "order123")
    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;
    @Schema(description = "Date and time when order was placed")
    @NotNull(message = "Order date cannot be null")
    private Instant orderDate;
    @Schema(description = "Short name of the delivery method", example = "Express")
    @NotBlank(message = "Delivery method cannot be blank")
    private String deliveryMethodShortName;
    @Schema(description = "Total order cost including shipping", example = "55.99")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total must be greater than 0")
    private double total;
    @Schema(description = "Current status of the order", example = "PENDING")
    @NotBlank(message = "Status cannot be blank")
    private String status;
    @Schema(description = "Subtotal price before shipping", example = "50.00")
    @DecimalMin(value = "0.0", inclusive = true, message = "Subtotal cannot be negative")
    private double subTotal;
    @Schema(description = "Shipping cost", example = "5.99")
    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping price cannot be negative")
    private double shippingPrice;
    @Schema(description = "List of items included in the order")
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemDTO> orderItems;

}
