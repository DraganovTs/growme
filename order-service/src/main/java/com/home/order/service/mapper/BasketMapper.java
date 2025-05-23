package com.home.order.service.mapper;

import com.home.order.service.model.dto.BasketData;
import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.BasketItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BasketMapper {

    public Basket mapBasketDataToBasket(Basket basket,BasketData basketData) {
        basket.setItems(basketData.getItems());
        basket.setDeliveryMethodId(basketData.getDeliveryMethodId());
        basket.setShippingPrice(basketData.getShippingPrice());
        return basket;
    }

    public BasketData mapBasketToBasketData(Basket save) {
        BasketData basketData = new BasketData();
        basketData.setId(save.getId());
        basketData.setItems(save.getItems());
        basketData.setShippingPrice(save.getShippingPrice());
        basketData.setDeliveryMethodId(save.getDeliveryMethodId());
        return basketData;
    }

    public List<BasketItemDTO> mapBasketItemsToBasketItemsDTO(List<BasketItem> items) {
        return items.stream()
                .map(this::mapBasketItemToBasketItemDTO)
                .collect(Collectors.toList());
    }

    private BasketItemDTO mapBasketItemToBasketItemDTO(BasketItem item) {
        return BasketItemDTO.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}
