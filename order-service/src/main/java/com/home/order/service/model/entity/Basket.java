package com.home.order.service.model.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RedisHash("BASKET")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Basket {
    @NotNull(message = "Basket ID is required")
    @Size(max = 36, message = "Basket ID must not exceed 36 characters")
    private String id;
    @NotNull(message = "Basket items must not be null")
    @Size(min = 1, message = "Basket must contain at least one item")
    private List<BasketItem> items;

    private Integer deliveryMethodId;
    @DecimalMin(value = "0.0",  message = "Shipping price must be non-negative")
    private BigDecimal shippingPrice;
    private String clientSecret;

    private String  paymentIntentId;

    public Basket(String basketId) {
        this.id = basketId;
        this.items = new ArrayList<>();
    }
}
