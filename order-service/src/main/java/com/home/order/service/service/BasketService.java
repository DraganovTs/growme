package com.home.order.service.service;

import com.home.order.service.model.entity.Basket;

public interface BasketService {

    Basket createBasket();
    Basket getBasketById(String id);
    void deleteBasket(String id);
}
