package com.home.controller;

import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "/api/payments",produces = MediaType.APPLICATION_JSON)
public class PaymentController {

    @GetMapping("/test")
    public String test() {
        return "Payment service works fine";
    }
}
