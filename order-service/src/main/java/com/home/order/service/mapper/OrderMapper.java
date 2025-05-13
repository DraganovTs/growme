package com.home.order.service.mapper;

import com.home.order.service.model.entity.BasketItem;
import com.home.order.service.model.entity.OrderItem;
import com.home.order.service.model.entity.ProductItemOrdered;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public List<OrderItem> mapBasketItemsToOrderItems(List<BasketItem> basketItems, ProductResolver productResolver) {
        return basketItems.stream()
                .map(item -> mapBasketItemToOrderItem(item, productResolver.resolveProduct(item.getProductId())))
                .toList();
    }


    private OrderItem mapBasketItemToOrderItem(BasketItem item, Product product) {
        return new OrderItem(
                new ProductItemOrdered(product.getProductId(), product.getTitle(), product.getImageUrl()),
                item.getQuantity(),
                product.getUnitPrice()
        );
    }
}
