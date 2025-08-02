package com.home.growme.produt.service.service.impl;

import com.home.growme.produt.service.mapper.CategoryMapper;
import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWithProductsDTO;
import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.repository.CategoryRepository;
import com.home.growme.produt.service.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {
        return categoryMapper.mapCategoryToCategoryDTO(categoryRepository.save(categoryMapper.mapCategoryDTOToCategory(categoryDto)));
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
        return categoryRepository.findAll().stream().map(categoryMapper::mapCategoryToCategoryDTO).toList();
    }

    @Override
    public List<CategoryWithProductsDTO> getCategoriesProducts() {
        return List.of();
    }
}
