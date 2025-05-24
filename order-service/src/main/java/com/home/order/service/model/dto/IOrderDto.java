package com.home.order.service.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class IOrderDto {

    private String orderId;
    private String buyerEmail;
    private LocalDateTime orderDate;
    private AddressDTO shipToAddress;
    private String deliveryMethod;
    private BigDecimal shippingPrice;
    private List<OrderItemDTO> orderItems;
    private BigDecimal subTotal;
    private BigDecimal total;
    private String status;
}
