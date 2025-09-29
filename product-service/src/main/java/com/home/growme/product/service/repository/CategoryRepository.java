package com.home.growme.product.service.repository;

import com.home.growme.product.service.model.entity.Category;
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
