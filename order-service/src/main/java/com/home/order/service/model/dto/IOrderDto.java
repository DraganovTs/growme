package com.home.order.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;
    @Schema(description = "Email of the buyer", example = "buyer@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Buyer email cannot be blank")
    private String buyerEmail;
    @Schema(description = "Date and time when the order was placed")
    @NotNull(message = "Order date cannot be null")
    private LocalDateTime orderDate;
    @Schema(description = "Shipping address")
    @Valid
    @NotNull(message = "Shipping address cannot be null")
    private AddressDTO shipToAddress;
    @Schema(description = "Delivery method short name", example = "Express")
    @NotBlank(message = "Delivery method cannot be blank")
    private String deliveryMethod;
    @Schema(description = "Shipping price", example = "5.99")
    @NotNull(message = "Shipping price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Shipping price must be positive")
    private BigDecimal shippingPrice;
    @Schema(description = "List of order items")
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDTO> orderItems;
    @Schema(description = "Subtotal price before shipping", example = "50.00")
    @NotNull(message = "Subtotal cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Subtotal must be positive")
    private BigDecimal subTotal;
    @Schema(description = "Total price including shipping", example = "55.99")
    @NotNull(message = "Total cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total must be positive")
    private BigDecimal total;
    @Schema(description = "Order status", example = "PENDING")
    @NotBlank(message = "Order status cannot be blank")
    private String status;
}
