package com.home.growme.produt.service.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(name = "product_id")
    private UUID productId;
    @NotBlank(message = "Product name must not be blank")
    @Size(max = 30, message = "Product name must be at most 30 characters")
    @Column(name = "name", nullable = false)
    private String name;
    @Size(max = 30, message = "Brand name must be at most 30 characters")
    @Column(name = "brand")
    private String brand;
    @NotBlank(message = "Description must not be blank")
    @Size(max = 500, message = "Description must be at most 500 characters")
    @Column(name = "description", nullable = false)
    private String description;
    @NotNull(message = "Price must be provided")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid decimal with up to 2 fraction digits")
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "units_in_stock")
    @Min(value = 0, message = "Units in stock cannot be negative")
    private int unitsInStock;
    @Size(max = 500, message = "Image URL must be at most 500 characters")
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "date_created")
    @CreationTimestamp
    private Date dateCreated;
    @Column(name = "last_updated")
    @UpdateTimestamp
    private Date lastUpdated;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private Owner owner;


    @PrePersist
    public void generateId() {
        if (productId == null) {
            productId = UUID.randomUUID();
        }
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.unitsInStock < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + productId);
        }
        this.unitsInStock -= quantity;
    }
}
