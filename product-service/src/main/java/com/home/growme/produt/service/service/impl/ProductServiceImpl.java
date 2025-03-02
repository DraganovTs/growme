package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.model.dto.ProductDTO;
import com.home.growme.produt.service.model.entity.Product;
import com.home.growme.produt.service.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product createProduct(ProductDTO productDto) {
        return null;
    }

    @Override
    public Product updateProduct(UUID productId, ProductDTO productDto) {
        return null;
    }

    @Override
    public void deleteProduct(UUID productId) {

    }

    @Override
    public Product getProductById(UUID productId) {
        return null;
    }

    @Override
    public List<Product> getProductsByCategory(UUID categoryId) {
        return List.of();
    }

    @Override
    public List<Product> getProductsByOwner(UUID ownerId) {
        return List.of();
    }

    @Override
    public List<Product> getAllProducts() {
        return List.of();
    }
}
