package com.home.growme.common.module.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductInfo {
    private String id;
    private String name;
    private String imageUrl;
    private BigDecimal price;
}
