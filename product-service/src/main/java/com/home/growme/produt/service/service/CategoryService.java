package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWithProductsDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing categories and their associated operations.
 * Provides methods for creating, updating, deleting, and retrieving categories,
 * as well as retrieving categories along with their associated products.
 */
public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDto);
    CategoryDTO updateCategory(UUID categoryId, CategoryDTO categoryDto);
    void deleteCategory(UUID categoryId);
    List<CategoryDTO> getAllCategories();

}
