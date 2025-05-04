package com.home.order.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryMethodDTO {
    private Integer deliveryMethodId;
    private String shortName;
    private String deliveryTime;
    private String description;
    private BigDecimal price;
}
