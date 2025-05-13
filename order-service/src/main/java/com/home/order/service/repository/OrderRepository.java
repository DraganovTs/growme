package com.home.order.service.repository;

import com.home.order.service.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByBuyerEmail(String buyerEmail);

    Optional<Order> findOrderByOrderIdAndBuyerEmail(Integer orderId, String buyerEmail);

    Order findByPaymentIntentId(String paymentIntentId);
}
