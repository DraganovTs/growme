package com.home.order.service.mapper;

import com.home.order.service.feign.ProductServiceClient;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.order.service.model.dto.*;
import com.home.order.service.model.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final ProductServiceClient productServiceClient;

    public OrderMapper(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    public List<OrderItem> mapBasketItemsToOrderItems(List<BasketItem> basketItems) {
        if (basketItems == null) {
            throw new IllegalArgumentException("Basket items cannot be null");
        }

        return basketItems.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getProductId() != null && item.getQuantity() > 0)
                .map(this::createOrderItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private OrderItem createOrderItem(BasketItem basketItem) {
        try {
            ProductInfo product = productServiceClient.getProductInfo(basketItem.getProductId().toString());

            if (product == null || product.getPrice() == null) {
                throw new IllegalStateException("Invalid product data");
            }

            OrderItem orderItem = new OrderItem(
                    new ProductItemOrdered(
                            UUID.fromString(product.getId()),
                            product.getName(),
                            product.getImageUrl()
                    ),
                    basketItem.getQuantity(),
                    product.getPrice()
            );


            orderItem.setOrderItemId(UUID.fromString(product.getId()));
            return orderItem;

        } catch (Exception e) {
            System.err.printf("Failed to process product %s: %s%n",
                    basketItem.getProductId(), e.getMessage());
            return null;
        }
    }


    public IOrderDto IOrderDto(Order order) {

        return IOrderDto.builder()
                .orderId(order.getOrderId().toString())
                .buyerEmail(order.getBuyerEmail())
                .orderDate(LocalDateTime.ofInstant(order.getOrderDate(), ZoneId.systemDefault()))
                .shipToAddress(mapAddressToAddressDTO(order.getShipToAddress()))
                .deliveryMethod(order.getDeliveryMethod().getShortName())
                .shippingPrice(order.getDeliveryMethod().getPrice())
                .orderItems(mapOrderItemsToOrderItemsDTO(order.getOrderItems()))
                .subTotal(order.getSubTotal())
                .total(order.getTotal())
                .status(order.getStatus().toString())
                .build();
    }

    private List<OrderItemDTO> mapOrderItemsToOrderItemsDTO(List<OrderItem> orderItems) {
        return orderItems.stream().map(this::mapOrderItemToOrderItemDTO).collect(Collectors.toList());
    }

    private OrderItemDTO mapOrderItemToOrderItemDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .productId(orderItem.getOrderItemId().toString())
                .productName(orderItem.getItemOrdered().getProductName())
                .imageUrl(orderItem.getItemOrdered().getImageUrl())
                .unitPrice(orderItem.getPrice().doubleValue())
                .quantity(orderItem.getQuantity())
                .build();
    }

    private AddressDTO mapAddressToAddressDTO(Address shipToAddress) {
        return AddressDTO.builder()
                .firstName(shipToAddress.getFirstName())
                .lastName(shipToAddress.getLastName())
                .street(shipToAddress.getStreet())
                .city(shipToAddress.getCity())
                .state(shipToAddress.getState())
                .zipCode(shipToAddress.getZipCode())
                .build();
    }
}