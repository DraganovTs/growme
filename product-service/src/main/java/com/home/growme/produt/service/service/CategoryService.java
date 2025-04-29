package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWhitProductsDTO;
import com.home.growme.produt.service.model.entity.Category;

import java.util.List;
import java.util.UUID;

/**
 * The CategoryService interface provides methods to manage and interact with categories
 * in an application. It supports CRUD operations and the retrieval of categories and their
 * associated products.
 */
public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDto);
    Category updateCategory(UUID categoryId, CategoryDTO categoryDto);
    void deleteCategory(UUID categoryId);
    List<CategoryDTO> getAllCategories();
    List<CategoryWhitProductsDTO> getCategoriesProducts();

    //TODO implement
}
