package com.home.growme.produt.service.controller;

import com.home.growme.produt.service.model.dto.ProductDTO;
import com.home.growme.produt.service.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/products/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductDTO> getProduct(@RequestParam String id) {
        ProductDTO productDTO = productService.getProductById(id);

        return ResponseEntity.ok(productDTO);
    }
    //TODO
    //GET /products → Fetch all products
    //GET /products/{id} → Get a single product
    //POST /products → Add a new product
    //PUT /products/{id} → Update a product
    //DELETE /products/{id} → Remove a product

}
