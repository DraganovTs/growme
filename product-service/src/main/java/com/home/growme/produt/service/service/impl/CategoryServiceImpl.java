package com.home.growme.produt.service.service.impl;

import com.home.growme.common.module.exceptions.CategoryNotFoundException;
import com.home.growme.produt.service.exception.CategoryAlreadyExistException;
import com.home.growme.produt.service.mapper.CategoryMapper;
import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWithProductsDTO;
import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.repository.CategoryRepository;
import com.home.growme.produt.service.service.CategoryService;
import com.home.growme.produt.service.service.EventPublisherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventPublisherService eventPublisherService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, EventPublisherService eventPublisherService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.eventPublisherService = eventPublisherService;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDto) {
        if (categoryRepository.existsCategoryByCategoryName(categoryDto.getCategoryName())) {
            throw new CategoryAlreadyExistException("Category already exist whit name " + categoryDto.getCategoryName());
        }
        Category savedCategory = categoryRepository.save(categoryMapper.mapCategoryDTOToCategory(categoryDto));
        eventPublisherService.publishCategoryCreation(savedCategory.getCategoryId().toString(), savedCategory.getCategoryName());
        return categoryMapper.mapCategoryToCategoryDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(UUID categoryId, CategoryDTO categoryDto) {
        Category existingCategory = categoryRepository.findCategoryByCategoryId(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + categoryId));

        Category updatedCategory = categoryMapper.mapCategoryDTOToCategory(categoryDto);
        updatedCategory.setCategoryId(existingCategory.getCategoryId());


        return categoryMapper.mapCategoryToCategoryDTO(categoryRepository.save(updatedCategory));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category not found with ID: " + categoryId);
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::mapCategoryToCategoryDTO).toList();
    }


}
