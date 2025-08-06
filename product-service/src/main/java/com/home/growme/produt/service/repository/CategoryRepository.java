package com.home.growme.produt.service.repository;

import com.home.growme.produt.service.model.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByCategoryName(String name);

    boolean existsCategoryByCategoryName(String categoryName);

    Optional<Category> findCategoryByCategoryId(UUID categoryId);
}
