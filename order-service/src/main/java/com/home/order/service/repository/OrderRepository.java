package com.home.order.service.repository;

import com.home.order.service.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByBuyerEmail(String buyerEmail);

    Optional<Order> findOrderByOrderIdAndBuyerEmail(UUID orderId, String buyerEmail);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    Optional<Order> getOrderByOrderId(UUID uuid);

    Optional<Order> findBy(String basketId);
}
