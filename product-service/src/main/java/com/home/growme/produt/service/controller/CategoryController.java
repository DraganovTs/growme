package com.home.growme.produt.service.controller;

import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWhitProductsDTO;
import com.home.growme.produt.service.service.CategoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/categories",produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/allCategories")
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> categoryDTOList = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryDTOList);
    }


    @GetMapping("/allCategoriesWhitProducts")
    public ResponseEntity<List<CategoryWhitProductsDTO>> findAllWithProducts() {
        List<CategoryWhitProductsDTO> categoryWhitProductsDTOList = categoryService.getCategoriesProducts();
        return ResponseEntity.ok(categoryWhitProductsDTOList);
    }

}
