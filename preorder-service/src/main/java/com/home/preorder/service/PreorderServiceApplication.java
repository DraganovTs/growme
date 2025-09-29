package com.home.preorder.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PreorderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PreorderServiceApplication.class, args);
    }

}
