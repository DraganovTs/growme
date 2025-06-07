package com.home.order.service.service;

import com.home.order.service.model.dto.IOrderDto;
import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.Order;
import com.home.order.service.model.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining the operations for managing Orders within the system.
 * Provides methods for creating, updating, retrieving, and managing order-related operations
 * such as payment intents and order status updates.
 */
public interface OrderService {
    Basket createOrUpdatePaymentIntent(String basketId,String correlationId);

    Order createOrUpdateOrder( OrderDTO orderDTO,String correlationId);

    List<IOrderDto> getAllOrdersForUser(String userEmail);

    IOrderDto getOrderById(UUID uuid);

    void updateOrderStatusByPaymentIntentId(String paymentIntentId, OrderStatus orderStatus);


}
