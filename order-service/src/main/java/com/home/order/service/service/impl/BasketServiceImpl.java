package com.home.order.service.service.impl;

import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.mapper.BasketMapper;
import com.home.order.service.model.dto.BasketData;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.service.BasketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BasketServiceImpl implements BasketService {

    private final BasketRepository basketRepository;
    private final BasketMapper basketMapper;

    public BasketServiceImpl(BasketRepository basketRepository, BasketMapper basketMapper) {
        this.basketRepository = basketRepository;
        this.basketMapper = basketMapper;
    }

    @Override
    public BasketData createBasket(BasketData basketData) {
        Basket basket = basketMapper.mapBasketDataToBasket(basketData);
        return basketMapper.mapBasketToBasketData(basketRepository.save(basket));
    }

    @Override
    public BasketData getBasketById(String id) {

        Basket basket = basketRepository.findById(id).orElseThrow(
                () -> new BasketNotFoundException("Basket whit id {} not found")
        );

        return basketMapper.mapBasketToBasketData(basket);
    }

    @Override
    public void deleteBasket(String id) {
        basketRepository.deleteById(id);
    }
}
