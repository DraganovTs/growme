package com.home.growme.produt.service.controller;

import com.home.growme.produt.service.model.dto.ProductRequestDTO;
import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.dto.ProductResponseListDTO;
import com.home.growme.produt.service.service.ImageService;
import com.home.growme.produt.service.service.ProductService;
import com.home.growme.produt.service.specification.ProductSpecParams;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;

    public ProductController(ProductService productService, ImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable String id) {
        log.info("Fetching product with ID: {}", id);
        ProductResponseDTO productResponseDTO = productService.getProductById(id);
        return ResponseEntity.ok(productResponseDTO);
    }

    @GetMapping
    public ResponseEntity<ProductResponseListDTO> getProducts(ProductSpecParams request) {
        log.info("Fetching product list with params: {}", request);
        ProductResponseListDTO productResponseDTOList = productService.getAllProducts(request);
        return ResponseEntity.ok(productResponseDTOList);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO>createProduct(@RequestBody ProductRequestDTO productRequestDTO){
        log.info("Creating new product: {}", productRequestDTO);
        ProductResponseDTO responseDTO = productService.createProduct(productRequestDTO);
       return ResponseEntity.ok(responseDTO);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        log.info("Updating product with ID: {}", id);
        ProductResponseDTO productResponseDTO = productService.updateProduct(id,productRequestDTO);
        return ResponseEntity.ok(productResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("Uploading image: {}", file.getOriginalFilename());
        String imageUrl = imageService.saveImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/existing-images")
    public ResponseEntity<List<String>> getExistingImages() {
        log.info("Fetching existing images");
        List<String> imageUrls = imageService.getAllImages();
        return ResponseEntity.ok(imageUrls);
    }



}
