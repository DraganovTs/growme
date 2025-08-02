package com.home.growme.produt.service.service;


import com.home.growme.produt.service.mapper.CategoryMapper;
import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.entity.Category;
import com.home.growme.produt.service.model.entity.Owner;
import com.home.growme.produt.service.model.entity.Product;
import com.home.growme.produt.service.repository.CategoryRepository;
import com.home.growme.produt.service.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Category Service Tests")
@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTests {
    private static final String TEST_CATEGORY_NAME = "Test Category";
    private static final String UPDATED_CATEGORY_NAME = "Updated Category";
    private static final UUID GLOVES_PRODUCT_ID = UUID.fromString("223e4567-e89b-12d3-a456-426614174001");
    private static final UUID FERTILIZER_PRODUCT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private UUID categoryId;

    @BeforeEach
    void setup() {
        categoryId = UUID.randomUUID();
        category = createTestCategory();
        categoryDTO = createTestCategoryDTO();

    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName(TEST_CATEGORY_NAME);
        category.setProducts(createTestProducts());
        return category;
    }

    private List<Product> createTestProducts() {
        return List.of(
                createTestProduct(GLOVES_PRODUCT_ID, "Gardening Gloves", "PlantProtect",
                        "Durable gardening gloves with reinforced fingertips", new BigDecimal("12.50"), 50),
                createTestProduct(FERTILIZER_PRODUCT_ID, "Organic Fertilizer", "GrowGreen",
                        "Premium organic fertilizer for all plants, 5kg pack", new BigDecimal("24.99"), 100)
        );
    }

    private Product createTestProduct(UUID id, String name, String brand,
                                      String description, BigDecimal price, int stock) {
        return Product.builder()
                .productId(id)
                .name(name)
                .brand(brand)
                .description(description)
                .price(price)
                .unitsInStock(stock)
                .imageUrl("https://example.com/products/" + name.toLowerCase().replace(" ", "-") + ".jpg")
                .category(new Category())
                .owner(new Owner())
                .build();
    }

    private CategoryDTO createTestCategoryDTO() {
        return CategoryDTO.builder()
                .categoryId(categoryId)
                .categoryName(TEST_CATEGORY_NAME)
                .build();
    }



    @Nested
    class CreateCategoryTests {
        @Test
        @DisplayName("Should create and return category DTO")
        void shouldCreateCategory() {
            when(categoryMapper.mapCategoryDTOToCategory(categoryDTO)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.mapCategoryToCategoryDTO(category)).thenReturn(categoryDTO);

            CategoryDTO result = categoryService.createCategory(categoryDTO);

            assertEquals(TEST_CATEGORY_NAME, result.getCategoryName());
            verify(categoryRepository).save(category);
        }
    }

    @Nested
    class GetAllCategoriesTests {
        @Test
        @DisplayName("Should return list of category DTOs")
        void shouldReturnAllCategories() {
            List<Category> categories = List.of(category);
            when(categoryRepository.findAll()).thenReturn(categories);
            when(categoryMapper.mapCategoryToCategoryDTO(any(Category.class))).thenReturn(categoryDTO);

            List<CategoryDTO> result = categoryService.getAllCategories();

            assertEquals(1, result.size());
            assertEquals(TEST_CATEGORY_NAME, result.get(0).getCategoryName());
            verify(categoryRepository).findAll();
            verify(categoryMapper).mapCategoryToCategoryDTO(category);
        }

        @Test
        @DisplayName("Should return empty list when no categories found")
        void shouldReturnEmptyListWhenNoCategoriesExist() {
            when(categoryRepository.findAll()).thenReturn(List.of());

            List<CategoryDTO> result = categoryService.getAllCategories();

            assertTrue(result.isEmpty());
            verify(categoryRepository).findAll();
            verify(categoryMapper, never()).mapCategoryToCategoryDTO(any());
        }
    }

    @Nested
    class GetCategoriesProductsTests {
        @Test
        @DisplayName("Should return empty list as default implementation")
        void shouldReturnEmptyListOfCategoryWithProducts() {
            List<?> result = categoryService.getCategoriesProducts();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class DefensiveTests {
        @Test
        @DisplayName("Should throw NullPointerException if mapping DTO to entity fails")
        void shouldThrowWhenCategoryMappingFails() {
            when(categoryMapper.mapCategoryDTOToCategory(any())).thenThrow(new NullPointerException("Mapper failed"));

            assertThrows(NullPointerException.class, () -> categoryService.createCategory(categoryDTO));
        }
    }
//
//    @Nested
//    class UpdateCategoryTests {
//        @Test
//        @DisplayName("Should update existing category")
//        void shouldUpdateCategory() {
//            when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));
//            when(categoryRepository.save(any(Category.class))).thenReturn(category);
//
//            Category updated = categoryService.updateCategory(categoryId, categoryDTO);
//
//            assertEquals(TEST_CATEGORY_NAME, updated.getCategoryName());
//            verify(categoryRepository).findById(categoryId);
//            verify(categoryRepository).save(category);
//        }
//
//        @Test
//        @DisplayName("Should throw if category to update not found")
//        void shouldThrowWhenCategoryNotFoundForUpdate() {
//            when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());
//
//            Exception ex = assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(categoryId, categoryDTO));
//
//            assertTrue(ex.getMessage().contains("Category not found"));
//            verify(categoryRepository).findById(categoryId);
//            verify(categoryRepository, never()).save(any());
//        }
//    }
//
//    @Nested
//    class DeleteCategoryTests {
//        @Test
//        @DisplayName("Should delete category when it exists")
//        void shouldDeleteCategory() {
//            when(categoryRepository.existsById(categoryId)).thenReturn(true);
//
//            categoryService.deleteCategory(categoryId);
//
//            verify(categoryRepository).deleteById(categoryId);
//        }
//
//        @Test
//        @DisplayName("Should throw if category to delete not found")
//        void shouldThrowWhenCategoryNotFoundForDelete() {
//            when(categoryRepository.existsById(categoryId)).thenReturn(false);
//
//            Exception ex = assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(categoryId));
//
//            assertTrue(ex.getMessage().contains("Category not found"));
//            verify(categoryRepository, never()).deleteById(any());
//        }
//    }

}