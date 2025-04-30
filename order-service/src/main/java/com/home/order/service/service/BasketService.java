package com.home.order.service.service;

import com.home.order.service.model.dto.BasketData;

public interface BasketService {

    BasketData createBasket(BasketData basketData);
    BasketData getBasketById(String id);
    void deleteBasket(String id);
}
