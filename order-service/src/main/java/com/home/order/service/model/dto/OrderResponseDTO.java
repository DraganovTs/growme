package com.home.order.service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {

    private String orderId;
    private Instant orderDate;
    private String deliveryMethodShortName;
    private double total;
    private String status;
    private double subTotal;
    private double shippingPrice;
    private List<OrderItemDTO> orderItems;

}
