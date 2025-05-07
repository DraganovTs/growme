package com.home.order.service.service.impl;

import com.home.order.service.model.entity.Basket;
import com.home.order.service.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public Basket createOrUpdatePaymentIntent(String basketId) {
        return null;
    }
}
