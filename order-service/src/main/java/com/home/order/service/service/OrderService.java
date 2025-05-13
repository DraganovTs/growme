package com.home.order.service.service;

import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.Order;

public interface OrderService {
    Basket createOrUpdatePaymentIntent(String basketId);

    Order createOrUpdateOrder(String userEmail, OrderDTO orderDTO);
}
