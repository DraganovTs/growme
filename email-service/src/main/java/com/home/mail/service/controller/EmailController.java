package com.home.mail.service.controller;

import jakarta.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/emails",produces = MediaType.APPLICATION_JSON)
public class EmailController {


    @GetMapping("/test")
    public String test() {
        return "Email service works fine";
    }
}
