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
        log.info("Creating/updating basket with ID: {}", basketData.getId());

        if (basketData.getId() == null || basketData.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Basket ID cannot be null or empty");
        }

        Optional<Basket> basketOptional = basketRepository.findById(basketData.getId());
        Basket basket;

        if (basketOptional.isPresent()) {
            log.info("Updating existing basket with ID: {}", basketData.getId());
            basket = basketOptional.get();
            basket = basketMapper.mapBasketDataToBasket(basket, basketData);
        } else {
            log.info("Creating new basket with ID: {}", basketData.getId());
            basket = basketMapper.mapBasketDataToBasket(new Basket(basketData.getId()), basketData);
        }

        Basket savedBasket = basketRepository.save(basket);
        log.info("Basket saved successfully with ID: {}", savedBasket.getId());

        return basketMapper.mapBasketToBasketData(savedBasket);
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
