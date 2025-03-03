package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.exception.ProductNotFoundException;
import com.home.growme.produt.service.mapper.ProductMapper;
import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.entity.Product;
import com.home.growme.produt.service.repository.ProductRepository;
import com.home.growme.produt.service.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }


    @Override
    public ProductResponseDTO getProductById(String productId) {
        return productRepository.findById(UUID.fromString(productId))
                .map(productMapper::mapProductToProductResponseDTO)
                .orElseThrow(()->new ProductNotFoundException("Product with id " + productId + " not found"));
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return List.of();
    }




    @Override
    public Product createProduct(ProductResponseDTO productResponseDto) {
        return null;
    }

    @Override
    public Product updateProduct(UUID productId, ProductResponseDTO productResponseDto) {
        return null;
    }

    @Override
    public void deleteProduct(UUID productId) {

    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(UUID categoryId) {
        return List.of();
    }

    @Override
    public List<ProductResponseDTO> getProductsByOwner(UUID ownerId) {
        return List.of();
    }
}
