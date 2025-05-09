package com.home.order.service.model.dto;

import com.home.order.service.model.entity.BasketItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class BasketData {
    @NotNull
    private String id;
    @NotNull
    private List<BasketItem> items;

    private Integer deliveryMethodId;
    private BigDecimal shippingPrice;
    private String clientSecret;

    private String  paymentIntentId;

}
