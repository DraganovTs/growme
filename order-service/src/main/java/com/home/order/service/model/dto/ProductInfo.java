package com.home.order.service.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductInfo {
    private String id;
    private String title;
    private String imageUrl;
    private BigDecimal unitPrice;
}
