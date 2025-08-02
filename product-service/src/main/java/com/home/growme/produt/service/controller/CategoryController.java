package com.home.growme.produt.service.controller;

import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import com.home.growme.produt.service.model.dto.CategoryDTO;
import com.home.growme.produt.service.model.dto.CategoryWithProductsDTO;
import com.home.growme.produt.service.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing product categories in the GrowMe platform.
 * Provides endpoints for listing and creating categories, including with their associated products.
 */
@Tag(
        name = "Category Management API",
        description = """
        Set of REST APIs for managing product categories in the GrowMe platform.
        Allows operations such as:
        - Listing all categories
        - Viewing categories with their associated products
        - Creating new categories
        """
)
@RestController
@RequestMapping(value = "/api/categories",produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @Operation(
            summary = "Get all categories",
            description = "Returns a list of all product categories.",
            operationId = "getAllCategories"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> categoryDTOList = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryDTOList);
    }


    @Operation(
            summary = "Get all categories with products",
            description = "Returns a list of categories along with the products assigned to them.",
            operationId = "getCategoriesWithProducts"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of categories with products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryWithProductsDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/allCategoriesWhitProducts")
    public ResponseEntity<List<CategoryWithProductsDTO>> findAllWithProducts() {
        List<CategoryWithProductsDTO> categoryWithProductsDTOList = categoryService.getCategoriesProducts();
        return ResponseEntity.ok(categoryWithProductsDTOList);
    }

    @Operation(
            summary = "Create a new category",
            description = "Creates a new product category in the system.",
            operationId = "createCategory"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Category already exists", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategoryDTO = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(createdCategoryDTO);
    }

}
