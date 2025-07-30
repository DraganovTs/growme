package com.home.order.service.model.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Getter
@Builder
public class ProductItemOrdered {

    @NotNull(message = "Product item ID is required")
    private UUID productItemId;
    @NotNull(message = "Product name is required")
    @Size(max = 30, message = "Product name must not exceed 30 characters")
    private String productName;
    @NotNull(message = "Image URL is required")
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;


    public ProductItemOrdered(UUID productItemId, String productName, String imageUrl) {
        this.productItemId = productItemId;
        this.productName = productName;
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductItemOrdered that = (ProductItemOrdered) o;
        return productItemId == that.productItemId && Objects.equals(productName, that.productName) && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productItemId, productName, imageUrl);
    }
}