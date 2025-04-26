package com.home.growme.produt.service.mapper;

import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.entity.Category;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CategoryMapper {
    public CategoryDTO mapCategoryToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }

    public Category mapCategoryDTOToCategory(CategoryDTO categoryDto) {
        return Category.builder()
                .categoryId(UUID.randomUUID())
                .categoryName(categoryDto.getCategoryName())
                .build();
    }
}
