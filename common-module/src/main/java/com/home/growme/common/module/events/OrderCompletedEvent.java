package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.home.growme.common.module.dto.OrderItemDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OrderCompletedEvent  extends Event{
    private final String orderId;
    private final String orderUserId;
    private final String buyerEmail;
    private final List<OrderItemDTO> items;
    private final BigDecimal totalAmount;
    private  final Instant orderDate;

    @JsonCreator
    public OrderCompletedEvent(
            @JsonProperty("orderId") String orderId,
            @JsonProperty("orderUserId") String orderUserId,
            @JsonProperty("buyerEmail") String buyerEmail,
            @JsonProperty("items") List<OrderItemDTO> items,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("orderDate") Instant orderDate) {
        this.orderId = orderId;
        this.orderUserId = orderUserId;
        this.buyerEmail = buyerEmail;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }
}
