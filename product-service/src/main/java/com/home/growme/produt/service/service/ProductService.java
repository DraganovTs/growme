package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.ProductDTO;
import com.home.growme.produt.service.model.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product createProduct(ProductDTO productDto);
    Product updateProduct(UUID productId, ProductDTO productDto);
    void deleteProduct(UUID productId);
    Product getProductById(UUID productId);
    List<Product> getProductsByCategory(UUID categoryId);
    List<Product> getProductsByOwner(UUID ownerId);
    List<Product> getAllProducts();
    //TODO implement
}
