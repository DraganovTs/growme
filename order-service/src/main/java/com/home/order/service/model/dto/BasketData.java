package com.home.order.service.model.dto;

import com.home.order.service.model.entity.BasketItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BasketData {
    @NotNull
    private String id;
    @NotNull
    private List<BasketItem> items;
}
