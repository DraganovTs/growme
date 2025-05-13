package com.home.order.service.model.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
public class ProductItemOrdered {
    private long productItemId;
    private String productName;
    private String imageUrl;


    public ProductItemOrdered(long productItemId, @Nonnull String productName, @Nonnull String imageUrl) {
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