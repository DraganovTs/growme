package com.home.growme.common.module.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.home.growme.common.module.dto.OrderItemDTO;
import com.home.growme.common.module.dto.UserInfo;
import com.home.growme.common.module.enums.EmailType;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
public class OrderConfirmationEmailEvent extends EmailRequestEvent{
    private final String orderId;
    private final UserInfo userInfo;
    private final BigDecimal totalAmount;
    private final List<OrderItemDTO> items;

    @JsonCreator
    public OrderConfirmationEmailEvent(
            @JsonProperty("email") String email,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("userInfo") UserInfo userInfo,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("items") List<OrderItemDTO> items) {
        super(email, EmailType.ORDER_CONFIRMATION);
        this.orderId = orderId;
        this.userInfo = userInfo;
        this.totalAmount = totalAmount;
        this.items = List.copyOf(items);
    }
}
