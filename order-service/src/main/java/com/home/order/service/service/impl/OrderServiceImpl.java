package com.home.order.service.service.impl;

import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.exception.DeliveryMethodNotFoundException;
import com.home.order.service.exception.InvalidProductException;
import com.home.order.service.exception.PaymentProcessingException;
import com.home.order.service.feign.ProductServiceClient;
import com.home.order.service.mapper.BasketMapper;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.DeliveryMethod;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.repository.DeliveryMethodRepository;
import com.home.order.service.service.EventPublisherService;
import com.home.order.service.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


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
    @Transactional
    public Basket createOrUpdatePaymentIntent(String basketId) {

        Basket basket = validateAndPrepareBasket(basketId);
        BigDecimal amount = calculateTotalAmount(basket);


        try {
            CompletableFuture<PaymentIntentResponseEvent> paymentFuture =
                    isEmptyString(basket.getPaymentIntentId())
                            ? eventPublisherService.sendCreatePaymentIntent(basketId, amount)
                            : eventPublisherService.sendUpdatePaymentIntent(basketId, amount);

            PaymentIntentResponseEvent response = paymentFuture.join();

            basket.setPaymentIntentId(response.getPaymentIntentId());
            basket.setClientSecret(response.getClientSecret());
            return basketRepository.save(basket);

        } catch (CompletionException e) {
            throw new PaymentProcessingException("Failed to process payment intent");
        }
    }

    private boolean isEmptyString(String paymentIntentId) {
            return paymentIntentId == null || paymentIntentId.isEmpty();
    }

    private Basket validateAndPrepareBasket(String basketId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found: " + basketId));

        validateProducts(basket);
        return basket;
    }

    private void validateProducts(Basket basket) {
        List<ProductValidationResult> validationResults = productServiceClient
                .validateBasketItems(basketMapper.mapBasketItemsToBasketItemsDTO(basket.getItems()))
                .getBody();

        if (validationResults == null || validationResults.stream().anyMatch(r -> !r.isValid())) {
            throw new InvalidProductException("Invalid items in basket");
        }

        validationResults.forEach(r -> log.debug("Product validated: {}", r.getProductId()));
    }

    private BigDecimal calculateTotalAmount(Basket basket) {
        BigDecimal amount = basket.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (basket.getDeliveryMethodId() != null) {
            DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(basket.getDeliveryMethodId())
                    .orElseThrow(() -> new DeliveryMethodNotFoundException(
                            "Delivery method not found: " + basket.getDeliveryMethodId()));
            amount = amount.add(deliveryMethod.getPrice());
        }

        return amount;
    }
}