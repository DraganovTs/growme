package com.home.order.service.controller;

import com.home.order.service.model.entity.DeliveryMethod;
import com.home.order.service.service.DeliveryMethodService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/deliverymethods", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryMethodController {

    private final DeliveryMethodService deliveryMethodService;

    public DeliveryMethodController(DeliveryMethodService deliveryMethodService) {
        this.deliveryMethodService = deliveryMethodService;
    }

    @GetMapping
    public ResponseEntity<List<DeliveryMethod>> getAllDeliveryMethods() {
        List<DeliveryMethod> deliveryMethods = deliveryMethodService.getAllDeliveryMethods();
        return ResponseEntity.ok(deliveryMethods);
    }
}
