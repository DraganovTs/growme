package com.home.growme.product.service.service;

import com.home.growme.common.module.dto.BasketItemDTO;
import com.home.growme.common.module.dto.ProductInfo;
import com.home.growme.common.module.dto.ProductValidationResult;
import com.home.growme.common.module.events.OrderCompletedEvent;
import com.home.growme.product.service.model.dto.CategoryWithProductsDTO;
import com.home.growme.product.service.model.dto.ProductRequestDTO;
import com.home.growme.product.service.model.dto.ProductResponseDTO;
import com.home.growme.product.service.model.dto.ProductResponseListDTO;
import com.home.growme.product.service.specification.ProductSpecParams;

import java.util.List;

/**
 * Interface for managing product-related operations.
 * This service provides methods for CRUD operations, retrieving products by various criteria,
 * validating product-related requests, and handling product-related events.
 */
public interface ProductService {
    ProductResponseDTO getProductById(String productId);
    ProductResponseListDTO getAllProducts(ProductSpecParams specParams);

    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(String productId, ProductRequestDTO productRequestDTO);
    void deleteProduct(String productId, String ownerId);
    CategoryWithProductsDTO getProductsByCategory(ProductSpecParams specParams);
    ProductResponseListDTO getProductsByOwner(ProductSpecParams specParams);

    List<ProductValidationResult> validateProducts(List<BasketItemDTO> basketItems);

    ProductInfo getProductInfo(String productId);

    void completeOrder(OrderCompletedEvent event);

    List<CategoryWithProductsDTO> getCategoriesWithProducts();
}
