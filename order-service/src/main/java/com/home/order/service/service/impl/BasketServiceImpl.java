package com.home.order.service.service.impl;

import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.mapper.BasketMapper;
import com.home.order.service.model.dto.BasketData;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.service.BasketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public BasketData createOrUpdateBasket(BasketData basketData) {

        Optional<Basket> basketOptional = basketRepository.findById(basketData.getId());
        Basket basket= null;
        if (basketOptional.isPresent()){
            System.out.println("basket is present");
            basket = basketOptional.get();
        }else {
            System.out.println("basket is not present");
            basket = new Basket(basketData.getId());
        }

        basket = basketMapper.mapBasketDataToBasket(basket, basketData);
        basketRepository.save(basket);


        return basketMapper.mapBasketToBasketData(basketRepository.save(basket));
    }

    @Override
    public BasketData getBasketById(String id) {

        Basket basket = basketRepository.findById(id).orElseThrow(
                () -> new BasketNotFoundException("Basket whit id: {} not found" + id)
        );

        return basketMapper.mapBasketToBasketData(basket);
    }

    @Override
    public void deleteBasket(String id) {
        basketRepository.deleteById(id);
    }
}
