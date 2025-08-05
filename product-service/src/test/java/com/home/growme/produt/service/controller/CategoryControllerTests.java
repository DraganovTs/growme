package com.home.growme.produt.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.produt.service.exception.CategoryAlreadyExistException;
import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
public class CategoryControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDTO testCategoryDTO;

    @BeforeEach
    void setUp() {
        testCategoryDTO = CategoryDTO.builder()
                .categoryId(UUID.randomUUID())
                .categoryName("Fruits")
                .build();

    }

    @Test
    @DisplayName("GET /api/categories - Should return all categories successfully")
    void findAll_ShouldReturnAllCategories() throws Exception {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        testCategoryDTO.setCategoryId(categoryId);
        List<CategoryDTO> categories = Collections.singletonList(testCategoryDTO);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryId").value(categoryId.toString())) // Convert UUID to string
                .andExpect(jsonPath("$[0].categoryName").value(testCategoryDTO.getCategoryName()));

        verify(categoryService).getAllCategories();
    }


    @Test
    @DisplayName("POST /api/categories - Should create category successfully")
    void createCategory_ShouldCreateCategory() throws Exception {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        testCategoryDTO.setCategoryId(categoryId);
        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(testCategoryDTO);

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategoryDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.categoryId").value(categoryId.toString())) // Convert UUID to string for comparison
                .andExpect(jsonPath("$.categoryName").value(testCategoryDTO.getCategoryName()));

        // Verify
        ArgumentCaptor<CategoryDTO> categoryCaptor = ArgumentCaptor.forClass(CategoryDTO.class);
        verify(categoryService).createCategory(categoryCaptor.capture());

        CategoryDTO capturedCategory = categoryCaptor.getValue();
        assertEquals(testCategoryDTO.getCategoryName(), capturedCategory.getCategoryName());
    }

    @Test
    @DisplayName("POST /api/categories - Should return 400 for invalid input")
    void createCategory_ShouldReturnBadRequestForInvalidInput() throws Exception {
        // Arrange - Create invalid category (empty name)
        CategoryDTO invalidCategory = CategoryDTO.builder()
                .categoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .categoryName("invalid-name-invalid-name-invalid")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any());
    }

    @Test
    @DisplayName("GET /api/categories - Should return empty list when no categories exist")
    void findAll_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("GET /api/categories/allCategoriesWhitProducts - Should return empty list when no categories with products exist")
    void findAllWithProducts_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(categoryService.getCategoriesProducts()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/categories/allCategoriesWhitProducts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(categoryService).getCategoriesProducts();
    }

    @Test
    @DisplayName("POST /api/categories - Should return 409 when category already exists")
    void createCategory_ShouldReturnConflictWhenCategoryExists() throws Exception {
        // Arrange
        String errorMessage = "Category already exists";
        when(categoryService.createCategory(any(CategoryDTO.class)))
                .thenThrow(new CategoryAlreadyExistException(errorMessage));

        // Act & Assert
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategoryDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(categoryService).createCategory(any(CategoryDTO.class));
    }
}
