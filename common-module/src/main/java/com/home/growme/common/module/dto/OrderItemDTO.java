package com.home.growme.common.module.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@NoArgsConstructor
public class OrderItemDTO {
    private String productId;
    private int quantity;
    private BigDecimal price;


    @JsonCreator
    public OrderItemDTO(
            @JsonProperty("productId") String productId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("price") BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
}
