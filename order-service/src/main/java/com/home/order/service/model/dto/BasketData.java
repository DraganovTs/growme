package com.home.order.service.model.dto;

import com.home.order.service.model.entity.BasketItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "BasketData", description = "Basket with items and payment details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketData {

    @Schema(description = "Unique basket identifier", example = "basket123")
    @NotBlank(message = "Basket ID is required") // Use @NotBlank for strings to also reject empty/whitespace
    private String id;

    @Schema(description = "List of basket items")
    @NotNull(message = "Items list is required")
    @Size(min = 1, message = "Basket must contain at least one item")
    private List<@Valid BasketItem> items; // Also validate individual items in the list

    @Schema(description = "Selected delivery method ID", example = "1")
    @NotNull(message = "Delivery method ID is required")
    @Positive(message = "Delivery method ID must be positive")
    private Integer deliveryMethodId;

    @Schema(description = "Shipping price", example = "5.99")
    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping price must be 0 or greater")
    private BigDecimal shippingPrice;

    @Schema(description = "Payment client secret for payment processing")
    private String clientSecret;

    @Schema(description = "Payment intent ID from payment gateway")
    private String paymentIntentId;

}
