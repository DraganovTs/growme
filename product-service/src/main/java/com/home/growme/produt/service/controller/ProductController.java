package com.home.growme.produt.service.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/products/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    //TODO
    //GET /products → Fetch all products
    //GET /products/{id} → Get a single product
    //POST /products → Add a new product
    //PUT /products/{id} → Update a product
    //DELETE /products/{id} → Remove a product

}
