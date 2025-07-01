package com.home.preorder.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
}
