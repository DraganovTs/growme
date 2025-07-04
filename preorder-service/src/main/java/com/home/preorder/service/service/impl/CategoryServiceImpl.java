package com.home.preorder.service.service.impl;

import com.home.growme.common.module.exceptions.CategoryNotFoundException;
import com.home.preorder.service.model.entity.Category;
import com.home.preorder.service.repository.CategoryRepository;
import com.home.preorder.service.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findCategoryByCategoryName(name)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found whit name: " + name));
    }
}
