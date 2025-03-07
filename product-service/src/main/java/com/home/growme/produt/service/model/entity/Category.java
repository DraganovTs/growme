package com.home.growme.produt.service.model.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @Column(name = "category_id")
    private UUID categoryId;
    @Column(name = "category_name", nullable = false,unique = true)
    private String categoryName;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @PrePersist
    public void generateId() {
        if (categoryId == null) {
            categoryId = UUID.randomUUID();
        }
    }

}
