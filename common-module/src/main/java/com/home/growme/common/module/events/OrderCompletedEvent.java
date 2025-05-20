package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.home.growme.common.module.dto.OrderItemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class OrderCompletedEvent {
    private Integer orderId;
    private String buyerEmail;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private Instant orderDate;

    @JsonCreator
    public OrderCompletedEvent(
            @JsonProperty("orderId") Integer orderId,
            @JsonProperty("buyerEmail") String buyerEmail,
            @JsonProperty("items") List<OrderItemDTO> items,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("orderDate") Instant orderDate) {
        this.orderId = orderId;
        this.buyerEmail = buyerEmail;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }
}
