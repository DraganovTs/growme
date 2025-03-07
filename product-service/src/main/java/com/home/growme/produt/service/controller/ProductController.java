package com.home.growme.produt.service.controller;

import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.dto.ProductResponseListDTO;
import com.home.growme.produt.service.service.ProductService;
import com.home.growme.produt.service.specification.ProductSpecParams;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable String id) {
        ProductResponseDTO productResponseDTO = productService.getProductById(id);
        return ResponseEntity.ok(productResponseDTO);
    }

    @GetMapping
    public ResponseEntity<ProductResponseListDTO> getProducts(ProductSpecParams request) {
        ProductResponseListDTO productResponseDTOList = productService.getAllProducts(request);
        return ResponseEntity.ok(productResponseDTOList);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO>createProduct(@RequestBody ProductResponseDTO productResponseDTO){
       ProductResponseDTO responseDTO = productService.createProduct(productResponseDTO);
       return ResponseEntity.ok(responseDTO);
    }



    //TODO
    //POST /products → Add a new product
    //PUT /products/{id} → Update a product
    //DELETE /products/{id} → Remove a product

}
