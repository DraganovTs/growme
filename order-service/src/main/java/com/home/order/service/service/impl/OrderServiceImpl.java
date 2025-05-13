package com.home.order.service.service.impl;

import com.home.growme.common.module.events.PaymentIntentResponseEvent;
import com.home.order.service.exception.BasketNotFoundException;
import com.home.order.service.exception.DeliveryMethodNotFoundException;
import com.home.order.service.exception.InvalidProductException;
import com.home.order.service.exception.PaymentProcessingException;
import com.home.order.service.feign.ProductServiceClient;
import com.home.order.service.mapper.BasketMapper;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.order.service.mapper.OrderMapper;
import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.entity.*;
import com.home.order.service.model.enums.OrderStatus;
import com.home.order.service.repository.BasketRepository;
import com.home.order.service.repository.DeliveryMethodRepository;
import com.home.order.service.repository.OrderRepository;
import com.home.order.service.service.EventPublisherService;
import com.home.order.service.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
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
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(BasketRepository basketRepository, DeliveryMethodRepository deliveryMethodRepository,
                            ProductServiceClient productServiceClient, BasketMapper basketMapper,
                            EventPublisherService eventPublisherService, OrderRepository orderRepository, OrderMapper orderMapper) {
        this.basketRepository = basketRepository;
        this.deliveryMethodRepository = deliveryMethodRepository;
        this.productServiceClient = productServiceClient;
        this.basketMapper = basketMapper;
        this.eventPublisherService = eventPublisherService;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
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

    @Override
    public Order createOrUpdateOrder(String userEmail, OrderDTO orderDTO) {
        Basket basket = getBasket(orderDTO.getBasketId());
        Address address = extractAddress(orderDTO);
        DeliveryMethod deliveryMethod = getDeliveryMethod(orderDTO.getDeliveryMethodId());

        Order existingOrder = orderRepository.findByPaymentIntentId(basket.getPaymentIntentId());
        if (existingOrder != null && "succeeded".equals(existingOrder.getPaymentIntentId())) {
            throw new IllegalStateException("Order cannot be modified as it is already paid.");
        }

        return saveNewOrder(userEmail, basket, address, deliveryMethod);
    }

    private Order saveNewOrder(String userEmail, Basket basket, Address address, DeliveryMethod deliveryMethod) {
        List<OrderItem> orderItems = mapOrderItems(basket);
        BigDecimal subTotal = calculateSubTotal(orderItems);

        Order order = Order.builder()
                .buyerEmail(userEmail)
                .deliveryMethod(deliveryMethod)
                .orderItems(orderItems)
                .shipToAddress(address)
                .subTotal(subTotal)
                .status(OrderStatus.PENDING)
                .paymentIntentId(basket.getPaymentIntentId())
                .orderDate(Instant.now())
                .build();

        return orderRepository.save(order);
    }

    private List<OrderItem> mapOrderItems(Basket basket) {
        return orderMapper.mapBasketItemsToOrderItems(basket.getItems());
    }

    private DeliveryMethod getDeliveryMethod(int deliveryMethodId) {
        return deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(()-> new DeliveryMethodNotFoundException("Delivery method not found with ID: " + deliveryMethodId));
    }

    private Address extractAddress(OrderDTO orderDTO) {
        return new Address(
                orderDTO.getShipToAddress().getFirstName(),
                orderDTO.getShipToAddress().getLastName(),
                orderDTO.getShipToAddress().getCity(),
                orderDTO.getShipToAddress().getState(),
                orderDTO.getShipToAddress().getStreet(),
                orderDTO.getShipToAddress().getZipCode()
        );
    }

    private Basket getBasket(String basketId) {
        return basketRepository.findById(basketId)
                .orElseThrow(()->new BasketNotFoundException("Basket not found with ID: " + basketId));
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

private BigDecimal calculateSubTotal(List<OrderItem> orderItems) {
    if (orderItems == null) {
        return BigDecimal.ZERO;
    }
    
    return orderItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}
}