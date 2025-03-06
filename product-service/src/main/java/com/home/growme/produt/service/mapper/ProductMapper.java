package com.home.growme.produt.service.mapper;

import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponseDTO mapProductToProductResponseDTO(Product product) {


            if (product.getCategory() == null) {
                System.out.println("Product ID: " + product.getProductId() + " has a NULL category!");
            } else {
                System.out.println("Product ID: " + product.getProductId() + " Category: " + product.getCategory().getCategoryName());
            }



        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .price(product.getPrice())
                .unitsInStock(product.getUnitsInStock())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory().getCategoryName())
                .ownerName(product.getOwner().getOwnerName())
                .ownerId(product.getOwner().getOwnerId())
                .productCategoryId(product.getCategory().getCategoryId())
                .build();
    }
}
