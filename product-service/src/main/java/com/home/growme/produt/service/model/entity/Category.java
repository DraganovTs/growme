package com.home.growme.produt.service.model.entity;

import com.home.growme.common.module.valuobjects.CategoryId;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @EmbeddedId
    private CategoryId id;
    @Column(name = "category_name", nullable = false,unique = true)
    private String categoryName;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
}
