package com.home.order.service.service;

import com.home.order.service.model.dto.BasketData;

public interface BasketService {

    BasketData createOrUpdateBasket(BasketData basketData);
    BasketData getBasketById(String id);
    void deleteBasket(String id);
}
