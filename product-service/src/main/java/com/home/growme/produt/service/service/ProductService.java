package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponseDTO getProductById(String productId);
    List<ProductResponseDTO> getAllProducts();

    //TODO implement
    Product createProduct(ProductResponseDTO productResponseDto);
    Product updateProduct(UUID productId, ProductResponseDTO productResponseDto);
    void deleteProduct(UUID productId);
    List<ProductResponseDTO> getProductsByCategory(UUID categoryId);
    List<ProductResponseDTO> getProductsByOwner(UUID ownerId);

}
