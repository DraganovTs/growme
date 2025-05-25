package com.home.growme.produt.service.controller;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.growme.produt.service.model.dto.*;
import com.home.growme.produt.service.model.entity.Product;
import com.home.growme.produt.service.service.ImageService;
import com.home.growme.produt.service.service.ProductService;
import com.home.growme.produt.service.specification.ProductSpecParams;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    @Value("${app.upload.dir}")
    private String uploadDir;

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

    @GetMapping("/seller")
    public ResponseEntity<ProductResponseListDTO> getProductsByOwner(@ModelAttribute ProductSpecParams specParams) {

        ProductResponseListDTO productResponseDTOList = productService.getProductsByOwner(specParams);
        return ResponseEntity.ok(productResponseDTOList);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        log.info("Creating new product: {}", productRequestDTO);
        ProductResponseDTO responseDTO = productService.createProduct(productRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        log.info("Updating product with ID: {} ", id);
        ProductResponseDTO productResponseDTO = productService.updateProduct(id, productRequestDTO);
        return ResponseEntity.ok(productResponseDTO);
    }

    @DeleteMapping("/{productId}/{userId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String productId,
            @PathVariable String userId
    ) {
        productService.deleteProduct(productId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<List<ProductValidationResult>> validateProducts(@Valid @RequestBody List<BasketItemDTO> basketItems){
        List<ProductValidationResult> results = productService.validateProducts(basketItems);
        return ResponseEntity.ok(results);
    }

    @GetMapping("productinfo/{productId}")
    public ResponseEntity<ProductInfo>getProductInfo(@PathVariable String productId){
        ProductInfo productInfo = productService.getProductInfo(productId);
        return ResponseEntity.ok(productInfo);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file) {
        String filename = imageService.uploadImage(file);
        return ResponseEntity.ok(new ImageUploadResponse(
                filename,          // ← Must include this
                file.getOriginalFilename(),
                "/images/" + filename
        ));
    }

    @GetMapping("/images")
    public ResponseEntity<List<String>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            byte[] imageBytes = Files.readAllBytes(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/images/recent")
    public ResponseEntity<List<ImageDisplayDTO>> getRecentImages() {
        return ResponseEntity.ok(imageService.getRecentImagesForDisplay());
    }

}
