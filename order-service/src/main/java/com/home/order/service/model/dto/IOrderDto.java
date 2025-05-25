package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "IOrderDto", description = "Order details including items and shipping")
@Data
@Builder
public class IOrderDto {
    @Schema(description = "Unique order identifier", example = "order123")
    private String orderId;
    @Schema(description = "Email of the buyer", example = "buyer@example.com")
    private String buyerEmail;
    @Schema(description = "Date and time when the order was placed")
    private LocalDateTime orderDate;
    @Schema(description = "Shipping address")
    private AddressDTO shipToAddress;
    @Schema(description = "Delivery method short name", example = "Express")
    private String deliveryMethod;
    @Schema(description = "Shipping price", example = "5.99")
    private BigDecimal shippingPrice;
    @Schema(description = "List of order items")
    private List<OrderItemDTO> orderItems;
    @Schema(description = "Subtotal price before shipping", example = "50.00")
    private BigDecimal subTotal;
    @Schema(description = "Total price including shipping", example = "55.99")
    private BigDecimal total;
    @Schema(description = "Order status", example = "PENDING")
    private String status;
}
