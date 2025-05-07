package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.PaymentIntentRequest;
import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.exception.InvalidProductException;
import com.home.order.service.feign.ProductServiceClient;
import com.home.order.service.mapper.BasketMapper;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.BasketItem;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.service.EventPublisherService;
import com.home.order.service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final BasketRepository basketRepository;
    private final ProductServiceClient productServiceClient;
    private final BasketMapper basketMapper;
    private final EventPublisherService eventPublisherService;

    public OrderServiceImpl(BasketRepository basketRepository, ProductServiceClient productServiceClient, BasketMapper basketMapper, EventPublisherService eventPublisherService) {
        this.basketRepository = basketRepository;
        this.productServiceClient = productServiceClient;
        this.basketMapper = basketMapper;
        this.eventPublisherService = eventPublisherService;
    }

    @Override
    public Basket createOrUpdatePaymentIntent(String basketId) {

//        Basket basket = basketRepository.findById(basketId).orElseThrow(
//                ()-> new BasketNotFoundException("Basket whit id: {} not found" + basketId)
//        );

        Basket basket = new Basket(basketId);
        basket.setItems(List.of(
                new BasketItem(
                        UUID.fromString("3dd0199d-6627-46e0-adb4-6308606e4cd7"),
                        2,
                        "asdasddas",
                        11,
                        "apple_1744301787397.jpg",
                        new BigDecimal("31.00"),
                        "asdsad",
                        "asdasdasdsad"
                ),
                new BasketItem(
                        UUID.fromString("958e18f0-bf1d-48d8-92f6-a45f57e3a630"),
                        1,
                        "taasdad",
                        12,
                        "rakia_1744221119435.jpg",
                        new BigDecimal("12.00"),
                        "1223",
                        "ssadasda as das as"
                )
        ));

        List<ProductValidationResult> validationResults = productServiceClient
                .validateBasketItems(basketMapper.mapBasketItemsToBasketItemsDTO(basket.getItems()))
                .getBody();

        if (validationResults == null || validationResults.stream().anyMatch(r->!r.isValid())){
            throw new InvalidProductException("Invalid items in basket");
        } else {
            validationResults.forEach(r->log.info("Product: {} is valid", r.getProductId()));
        }


        BigDecimal amount = basket.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (basket.getShippingPrice() != null) {
            amount = amount.add(basket.getShippingPrice());
        }

        eventPublisherService.sendCreatePaymentIntent(basketId, amount);

        return basket;
    }
}
