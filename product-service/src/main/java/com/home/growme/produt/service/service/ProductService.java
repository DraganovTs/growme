package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.ProductRequestDTO;
import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.dto.ProductResponseListDTO;
import com.home.growme.produt.service.specification.ProductSpecParams;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponseDTO getProductById(String productId);
    ProductResponseListDTO getAllProducts(ProductSpecParams specParams);

    //TODO implement
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(String productId, ProductRequestDTO productRequestDTO);
    void deleteProduct(String productId);
    List<ProductResponseDTO> getProductsByCategory(UUID categoryId);
    ProductResponseListDTO getProductsByOwner(ProductSpecParams specParams);
}
