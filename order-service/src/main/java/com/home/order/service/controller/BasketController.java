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
    public ResponseEntity<Basket> createBasket(@RequestBody BasketData basketData){
        Basket basket = basketService.createBasket();
        return ResponseEntity.ok(basket);
    }

    @GetMapping("/id")
    public ResponseEntity<Basket> getBasketById(@PathVariable String id){
        Basket basket = basketService.getBasketById(id);
        if (basket != null){
            return ResponseEntity.ok(basket);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/id")
    public ResponseEntity<Void> deleteBasket(@PathVariable String id){
        basketService.deleteBasket(id);
        return ResponseEntity.noContent().build();
    }

}
