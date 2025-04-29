package com.home.order.service.service.impl;

import com.home.order.service.model.entity.Basket;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.service.BasketService;
import org.springframework.stereotype.Service;

@Service
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;

    public BasketServiceImpl(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Override
    public Basket createBasket() {
        return null;
    }

    @Override
    public Basket getBasketById(String id) {
        return null;
    }

    @Override
    public void deleteBasket(String id) {

    }
}
