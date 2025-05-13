package com.home.order.service.mapper;

import com.home.order.service.feign.ProductServiceClient;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.order.service.model.entity.BasketItem;
import com.home.order.service.model.entity.OrderItem;
import com.home.order.service.model.entity.ProductItemOrdered;
import org.springframework.stereotype.Component;

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

            return new OrderItem(
                    new ProductItemOrdered(
                            UUID.fromString(product.getId()),
                            product.getName(),
                            product.getImageUrl()
                    ),
                    basketItem.getQuantity(),
                    product.getPrice()
            );
        } catch (Exception e) {
            System.err.printf("Failed to process product %s: %s%n",
                    basketItem.getProductId(), e.getMessage());
            return null;
        }
    }
}