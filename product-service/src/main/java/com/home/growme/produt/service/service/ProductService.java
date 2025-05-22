package com.home.growme.produt.service.service;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.produt.service.model.dto.ProductRequestDTO;
import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.dto.ProductResponseListDTO;
import com.home.growme.produt.service.specification.ProductSpecParams;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponseDTO getProductById(String productId);
    ProductResponseListDTO getAllProducts(ProductSpecParams specParams);

    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(String productId, ProductRequestDTO productRequestDTO);
    void deleteProduct(String productId, String ownerId);
    List<ProductResponseDTO> getProductsByCategory(UUID categoryId);
    ProductResponseListDTO getProductsByOwner(ProductSpecParams specParams);

    List<ProductValidationResult> validateProducts(List<BasketItemDTO> basketItems);

    ProductInfo getProductInfo(String productId);

    void completeOrder(OrderCompletedEvent event);
}
