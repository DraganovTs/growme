package com.home.growme.produt.service.mapper;

import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDTO mapCategoryToCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
