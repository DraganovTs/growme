package com.home.order.service.controller;

import com.home.order.service.model.entity.Basket;
import com.home.order.service.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{basketId}")
    public ResponseEntity<Basket> createOrUpdatePaymentIntent(@PathVariable String basketId){

        Basket basket = orderService.createOrUpdatePaymentIntent(basketId);
        if (basket == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(basket);

    }
}
