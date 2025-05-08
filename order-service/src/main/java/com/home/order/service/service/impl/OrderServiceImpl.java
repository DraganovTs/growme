package com.home.order.service.service.impl;

import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.exception.DeliveryMethodNotFoundException;
import com.home.order.service.exception.InvalidProductException;
import com.home.order.service.feign.ProductServiceClient;
import com.home.order.service.mapper.BasketMapper;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.DeliveryMethod;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.repository.DeliveryMethodRepository;
import com.home.order.service.service.EventPublisherService;
import com.home.order.service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final BasketRepository basketRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final ProductServiceClient productServiceClient;
    private final BasketMapper basketMapper;
    private final EventPublisherService eventPublisherService;

    public OrderServiceImpl(BasketRepository basketRepository, DeliveryMethodRepository deliveryMethodRepository,
                            ProductServiceClient productServiceClient, BasketMapper basketMapper,
                            EventPublisherService eventPublisherService) {
        this.basketRepository = basketRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.productServiceClient = productServiceClient;
        this.basketMapper = basketMapper;
        this.eventPublisherService = eventPublisherService;
    }

    @Override
    public Basket createOrUpdatePaymentIntent(String basketId) {

        Basket basket = basketRepository.findById(basketId).orElseThrow(
                () -> new BasketNotFoundException("Basket whit id: {} not found" + basketId)
        );


        List<ProductValidationResult> validationResults = productServiceClient
                .validateBasketItems(basketMapper.mapBasketItemsToBasketItemsDTO(basket.getItems()))
                .getBody();

        if (validationResults == null || validationResults.stream().anyMatch(r -> !r.isValid())) {
            throw new InvalidProductException("Invalid items in basket");
        } else {
            validationResults.forEach(r -> log.info("Product: {} is valid", r.getProductId()));
        }


        BigDecimal amount = basket.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (basket.getDeliveryMethodId() != null) {
            DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(basket.getDeliveryMethodId())
                    .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery method with id "
                            + basket.getDeliveryMethodId() + " not found"));
            amount = amount.add(deliveryMethod.getPrice());
        }


        if (IsEmptyString(basket.getPaymentIntentId())) {
            eventPublisherService.sendCreatePaymentIntent(basketId, amount);
        } else {
            eventPublisherService.sendUpdatePaymentIntent(basketId,amount);
        }


        return basket;
    }

    private boolean IsEmptyString(String data) {
        return data == null || data.isEmpty();
    }
}