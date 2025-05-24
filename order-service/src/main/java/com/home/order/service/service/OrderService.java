package com.home.order.service.service;

import com.home.order.service.model.dto.IOrderDto;
import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.dto.OrderResponseDTO;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.Order;
import com.home.order.service.model.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Basket createOrUpdatePaymentIntent(String basketId);

    Order createOrUpdateOrder( OrderDTO orderDTO);

    List<IOrderDto> getAllOrdersForUser(String userEmail);

    IOrderDto getOrderById(UUID uuid);

    void updateOrderStatusByPaymentIntentId(String paymentIntentId, OrderStatus orderStatus);


}
