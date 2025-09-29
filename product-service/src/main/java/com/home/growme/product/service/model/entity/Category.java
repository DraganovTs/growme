package com.home.growme.product.service.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Category name must not be blank")
    @Size(max = 30, message = "Category name must be at most 30 characters")
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
