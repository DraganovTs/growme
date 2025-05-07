package com.home.order.service.service;

import com.home.order.service.model.entity.Basket;

public interface OrderService {
    Basket createOrUpdatePaymentIntent(String basketId);
}
