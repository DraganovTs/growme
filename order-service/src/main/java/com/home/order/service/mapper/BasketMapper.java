package com.home.order.service.mapper;

import com.home.order.service.model.dto.BasketData;
import com.home.order.service.model.entity.Basket;
import org.springframework.stereotype.Component;

@Component
public class BasketMapper {

    public Basket mapBasketDataToBasket(BasketData basketData) {
        Basket basket = new Basket(basketData.getId());
        basket.setItems(basketData.getItems());
        return basket;
    }

    public BasketData mapBasketToBasketData(Basket save) {

        BasketData basketData = new BasketData();
        basketData.setId(save.getId());
        basketData.setItems(save.getItems());
        return basketData;
    }
}
