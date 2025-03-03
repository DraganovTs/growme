package com.home.growme.produt.service.model.dto;



import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductResponseDTO {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int unitsInStock;
    private String imageUrl;
    private String categoryName;
    private String ownerName;
    private UUID ownerId;
    private UUID productCategoryId;
}
