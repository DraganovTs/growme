package com.home.order.service.model.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Getter
public class ProductItemOrdered {
    private UUID productItemId;
    @Size(max = 30, message = "Product name must not exceed 30 characters")
    private String productName;
    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;


    public ProductItemOrdered(UUID productItemId, @Nonnull String productName, @Nonnull String imageUrl) {
        this.productItemId = productItemId;
        this.productName = Objects.requireNonNull(productName);
        this.imageUrl = Objects.requireNonNull(imageUrl);
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