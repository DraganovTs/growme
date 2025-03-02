package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWhitProductsDTO;
import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {
        return null;
    }

    @Override
    public Category updateCategory(UUID categoryId, CategoryDTO categoryDto) {
        return null;
    }

    @Override
    public void deleteCategory(UUID categoryId) {

    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return List.of();
    }

    @Override
    public List<CategoryWhitProductsDTO> getCategoriesProducts() {
        return List.of();
    }
}
