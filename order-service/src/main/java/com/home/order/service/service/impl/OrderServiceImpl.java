package com.home.order.service.service.impl;

import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.exception.InvalidProductException;
import com.home.order.service.feign.ProductServiceClient;
import com.home.order.service.mapper.BasketMapper;
import com.home.order.service.model.dto.ProductValidationResult;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final BasketRepository basketRepository;
    private final ProductServiceClient productServiceClient;
    private final BasketMapper basketMapper;

    public OrderServiceImpl(BasketRepository basketRepository, ProductServiceClient productServiceClient, BasketMapper basketMapper) {
        this.basketRepository = basketRepository;
        this.productServiceClient = productServiceClient;
        this.basketMapper = basketMapper;
    }

    @Override
    public Basket createOrUpdatePaymentIntent(String basketId) {

        Basket basket = basketRepository.findById(basketId).orElseThrow(
                ()-> new BasketNotFoundException("Basket whit id: {} not found" + basketId)
        );

        List<ProductValidationResult> validationResults = productServiceClient
                .validateBasketItems(basketMapper.mapBasketItemsToBasketItemsDTO(basket.getItems()))
                .getBody();

        if (validationResults == null || validationResults.stream().anyMatch(r->!r.isValid())){
            throw new InvalidProductException("Invalid items in basket");
        }

        return null;
    }
}
