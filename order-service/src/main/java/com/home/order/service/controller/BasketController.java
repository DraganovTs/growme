package com.home.order.service.controller;

import com.home.order.service.model.dto.BasketData;
import com.home.order.service.model.entity.Basket;
import com.home.order.service.service.BasketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/basket" , produces = MediaType.APPLICATION_JSON_VALUE)
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping
    public ResponseEntity<BasketData> createBasket(@RequestBody BasketData basketData){
        BasketData savedBasked = basketService.createBasket(basketData);
        return ResponseEntity.ok(savedBasked);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasketData> getBasketById(@PathVariable String id){
        BasketData basket = basketService.getBasketById(id);

            return ResponseEntity.ok(basket);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasket(@PathVariable String id){
        basketService.deleteBasket(id);
        return ResponseEntity.noContent().build();
    }

}
