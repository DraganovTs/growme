package com.home.order.service.controller;

import com.home.order.service.model.dto.IOrderDto;
import com.home.order.service.model.dto.OrderDTO;
import com.home.order.service.model.dto.OrderResponseDTO;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.model.entity.Order;
import com.home.order.service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PostMapping
    public ResponseEntity<Order> createOrUpdateOrder(@Valid @RequestBody OrderDTO orderDTO){
        System.out.println("********");
        Order order = orderService.createOrUpdateOrder(orderDTO);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userEmail}")
    public  ResponseEntity<List<IOrderDto>> getAllOrderForUser(@PathVariable String userEmail ){
        List<IOrderDto> orderDTOList = orderService.getAllOrdersForUser(userEmail);
        if (orderDTOList == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderDTOList);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<IOrderDto> getOrderDetails(@PathVariable String orderId){
        IOrderDto iOrderDto = orderService.getOrderById(UUID.fromString(orderId));
        if (iOrderDto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(iOrderDto);
    }

}
