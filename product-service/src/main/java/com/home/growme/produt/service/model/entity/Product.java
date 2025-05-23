package com.home.growme.produt.service.model.entity;


import jakarta.persistence.*;
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
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "brand")
    private String brand;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "units_in_stock")
    private int unitsInStock;
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
