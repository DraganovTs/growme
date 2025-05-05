package com.home.order.service.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RedisHash("BASKET")
@Data
@NoArgsConstructor
public class Basket {

    private String id;
    private List<BasketItem> items;

    private Integer deliveryMethodId;
    private BigDecimal shippingPrice;
    private String clientSecret;

    private String  paymentIntentId;

    public Basket(String basketId) {
        this.id = basketId;
        this.items = new ArrayList<>();
    }
}
