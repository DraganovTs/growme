package com.home.order.service.model.dto;

import com.home.order.service.model.entity.BasketItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "BasketData", description = "Basket with items and payment details")
@Data
@NoArgsConstructor
public class BasketData {

    @Schema(description = "Unique basket identifier", example = "basket123")
    @NotNull
    private String id;
    @Schema(description = "List of basket items")
    @NotNull
    private List<BasketItem> items;
    @Schema(description = "Selected delivery method ID", example = "1")
    private Integer deliveryMethodId;
    @Schema(description = "Shipping price", example = "5.99")
    private BigDecimal shippingPrice;
    @Schema(description = "Payment client secret for payment processing")
    private String clientSecret;
    @Schema(description = "Payment intent ID from payment gateway")
    private String  paymentIntentId;

}
