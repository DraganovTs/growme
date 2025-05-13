package com.home.growme.produt.service.mapper;

import com.home.growme.common.module.dto.ProductInfo;
import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.growme.produt.service.model.dto.OwnerDTO;
import com.home.growme.produt.service.model.dto.ProductRequestDTO;
import com.home.growme.produt.service.model.dto.ProductResponseDTO;
import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.model.entity.Owner;
import com.home.growme.produt.service.model.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
public class ProductMapper {


    public static Owner mapUserCreatedEventToOwner(UserCreatedEvent event) {
        return Owner.builder()
                .ownerId(UUID.fromString(event.getUserId()))
                .ownerName(event.getUserName())
                .products(new ArrayList<>())
                .build();
    }



    public ProductResponseDTO mapProductToProductResponseDTO(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        log.info("Mapping Product ID: {}", product.getProductId());

        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .price(product.getPrice())
                .unitsInStock(product.getUnitsInStock())
                .imageUrl(product.getImageUrl() != null ? product.getImageUrl() : "No image available")
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : "Uncategorized")
                .ownerName(product.getOwner() != null ? product.getOwner().getOwnerName() : "Unknown Owner")
                .ownerId(product.getOwner() != null ? product.getOwner().getOwnerId() : null)
                .productCategoryId(product.getCategory() != null ? product.getCategory().getCategoryId() : null)
                .build();
    }

    public Product mapProductRequestDTOToProduct(ProductRequestDTO productRequestDTO, Category category, Owner owner) {
        if (productRequestDTO == null) {
            throw new IllegalArgumentException("ProductRequestDTO cannot be null");
        }

        log.info("Mapping ProductRequestDTO: {}", productRequestDTO.getName());

        return Product.builder()
                .name(productRequestDTO.getName())
                .brand(productRequestDTO.getBrand())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .unitsInStock(productRequestDTO.getUnitsInStock())
                .imageUrl(productRequestDTO.getImageUrl()) // Image URL is now handled
                .category(category)
                .owner(owner)
                .build();
    }


    public OwnerDTO maOwnerToOwnerDTO(Owner owner) {
        return OwnerDTO.builder()
                .ownerId(owner.getOwnerId())
                .ownerName(owner.getOwnerName())
                .build();
    }
}
