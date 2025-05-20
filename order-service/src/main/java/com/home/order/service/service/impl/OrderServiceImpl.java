package com.home.order.service.service.impl;

import com.home.growme.common.module.dto.OrderItemDTO;
import com.home.growme.common.module.events.OrderCompletedEvent;
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

    public OrderServiceImpl(BasketRepository basketRepository,
                            DeliveryMethodRepository deliveryMethodRepository,
                            ProductServiceClient productServiceClient,
                            BasketMapper basketMapper,
                            EventPublisherService eventPublisherService,
                            OrderRepository orderRepository,
                            OrderMapper orderMapper) {
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
        Basket basket = fetchValidatedBasket(basketId);
        BigDecimal totalAmount = calculateTotalAmount(basket);

        try {
            PaymentIntentResponseEvent response = isNullOrEmpty(basket.getPaymentIntentId())
                    ? eventPublisherService.sendCreatePaymentIntent(basketId, totalAmount).join()
                    : eventPublisherService.sendUpdatePaymentIntent(basketId, totalAmount).join();

            basket.setPaymentIntentId(response.getPaymentIntentId());
            basket.setClientSecret(response.getClientSecret());

            return basketRepository.save(basket);
        } catch (CompletionException e) {
            throw new PaymentProcessingException("Failed to process payment intent");
        }
    }

    @Override
    @Transactional
    public Order createOrUpdateOrder(OrderDTO orderDTO) {
        Basket basket = fetchBasket(orderDTO.getBasketId());
        Address address = mapToAddress(orderDTO);
        DeliveryMethod deliveryMethod = fetchDeliveryMethod(orderDTO.getDeliveryMethodId());

        Order existingOrder = orderRepository.findByPaymentIntentId(basket.getPaymentIntentId());
        if (existingOrder != null && "succeeded".equalsIgnoreCase(existingOrder.getPaymentIntentId())) {
            throw new IllegalStateException("Order already paid and cannot be modified.");
        }

        Order order = persistNewOrder(orderDTO.getUserEmail(), basket, address, deliveryMethod);

        publishOrderCompletedEvent(order);

        return order;
    }

    private void publishOrderCompletedEvent(Order order) {
        List<OrderItemDTO> itemDTOS = order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                    item.getOrderItemId().toString(),
                        item.getQuantity(),
                        item.getPrice()
                )).toList();

        BigDecimal totalAmount = order.getSubTotal().add(order.getDeliveryMethod().getPrice());

        OrderCompletedEvent event = new OrderCompletedEvent(
                order.getOrderId(),
                order.getBuyerEmail(),
                itemDTOS,
                totalAmount,
                order.getOrderDate()
        );

        eventPublisherService.publishCompletedOrder(event);
    }

    private Basket fetchValidatedBasket(String basketId) {
        Basket basket = fetchBasket(basketId);
        validateBasketItems(basket);
        return basket;
    }

    private Basket fetchBasket(String basketId) {
        return basketRepository.findById(basketId)
                .orElseThrow(() -> new BasketNotFoundException("Basket not found: " + basketId));
    }


    private void validateBasketItems(Basket basket) {
        var validationResults = productServiceClient
                .validateBasketItems(basketMapper.mapBasketItemsToBasketItemsDTO(basket.getItems()))
                .getBody();

        if (validationResults == null || validationResults.stream().anyMatch(r -> !r.isValid())) {
            throw new InvalidProductException("Invalid items in basket");
        }

        validationResults.forEach(r -> log.debug("Validated product: {}", r.getProductId()));
    }

    private BigDecimal calculateTotalAmount(Basket basket) {
        BigDecimal itemTotal = basket.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (basket.getDeliveryMethodId() != null) {
            DeliveryMethod method = fetchDeliveryMethod(basket.getDeliveryMethodId());
            itemTotal = itemTotal.add(method.getPrice());
        }

        return itemTotal;
    }

    private DeliveryMethod fetchDeliveryMethod(int id) {
        return deliveryMethodRepository.findById(id)
                .orElseThrow(() -> new DeliveryMethodNotFoundException("Delivery method not found: " + id));
    }


    private Address mapToAddress(OrderDTO dto) {
        var addr = dto.getShipToAddress();
        return new Address(
                addr.getFirstName(),
                addr.getLastName(),
                addr.getCity(),
                addr.getState(),
                addr.getStreet(),
                addr.getZipCode()
        );
    }

    private Order persistNewOrder(String email, Basket basket, Address address, DeliveryMethod method) {
        List<OrderItem> items = orderMapper.mapBasketItemsToOrderItems(basket.getItems());
        BigDecimal subtotal = calculateSubTotal(items);

        Order order = Order.builder()
                .buyerEmail(email)
                .deliveryMethod(method)
                .orderItems(items)
                .shipToAddress(address)
                .subTotal(subtotal)
                .status(OrderStatus.PENDING)
                .paymentIntentId(basket.getPaymentIntentId())
                .orderDate(Instant.now())
                .build();

        return orderRepository.save(order);
    }

    private BigDecimal calculateSubTotal(List<OrderItem> items) {
        return items == null ? BigDecimal.ZERO :
                items.stream()
                        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }


}