package com.home.growme.produt.service.service;

import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWhitProductsDTO;
import com.home.growme.produt.service.model.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDto);
    Category updateCategory(UUID categoryId, CategoryDTO categoryDto);
    void deleteCategory(UUID categoryId);
    List<CategoryDTO> getAllCategories();
    List<CategoryWhitProductsDTO> getCategoriesProducts();

    //TODO implement
}
