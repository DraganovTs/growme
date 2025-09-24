package com.home.preorder.service.service;

import com.home.preorder.service.model.entity.Category;

public interface CategoryService {

    Category getCategoryByName(String name);

    void createCategory(String categoryId, String categoryName);

    boolean existCategoryByName(String categoryName);
}
