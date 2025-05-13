package com.home.order.service.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String imageUrl;
    private double unitPrice;
    private Integer quantity;

}
