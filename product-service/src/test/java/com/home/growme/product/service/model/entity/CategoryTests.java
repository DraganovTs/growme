package com.home.growme.product.service.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Entity Tests")
class CategoryTests {
    private static final String VALID_CATEGORY_NAME = "Electronics";
    private static final int MAX_NAME_LENGTH = 30;
    private static final String BLANK_ERROR_MESSAGE = "must not be blank";
    private static final String LENGTH_ERROR_MESSAGE = "must be at most 30 characters";

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Category.CategoryBuilder createValidCategoryBuilder() {
        return Category.builder()
                .categoryId(UUID.randomUUID())
                .categoryName(VALID_CATEGORY_NAME);
    }

    private Set<ConstraintViolation<Category>> validateCategory(Category category) {
        return validator.validate(category);
    }

    private boolean hasViolationMessage(Set<ConstraintViolation<Category>> violations, String message) {
        return violations.stream().anyMatch(v -> v.getMessage().contains(message));
    }

    @Nested
    @DisplayName("Valid Category Tests")
    class ValidCategoryTests {
        @Test
        @DisplayName("Should pass with valid category")
        void shouldPassWithValidCategory() {
            Category category = createValidCategoryBuilder().build();

            Set<ConstraintViolation<Category>> violations = validateCategory(category);

            assertTrue(violations.isEmpty(), "Valid category should have no violations");
        }

        @Test
        @DisplayName("Should pass when category ID is generated automatically")
        void shouldPassWhenCategoryIdIsGenerated() {
            Category category = Category.builder()
                    .categoryName(VALID_CATEGORY_NAME)
                    .build();
            category.generateId();

            Set<ConstraintViolation<Category>> violations = validateCategory(category);

            assertTrue(violations.isEmpty(), "Category with generated ID should have no violations");
            assertNotNull(category.getCategoryId(), "Category ID should be generated");
        }

        @Test
        @DisplayName("Should pass with null ID before generation")
        void shouldPassWithNullIdBeforeGeneration() {
            Category category = Category.builder()
                    .categoryName(VALID_CATEGORY_NAME)
                    .build();

            assertNull(category.getCategoryId(), "Category ID should be null before generation");
            assertTrue(validateCategory(category).isEmpty(), "Category should be valid with null ID");
        }
    }

    @Nested
    @DisplayName("Invalid Category Tests")
    class InvalidCategoryTests {
        @Test
        @DisplayName("Should fail when category name is blank")
        void shouldFailWhenCategoryNameIsBlank() {
            Category category = createValidCategoryBuilder()
                    .categoryName("")
                    .build();

            Set<ConstraintViolation<Category>> violations = validateCategory(category);

            assertFalse(violations.isEmpty(), "Blank category name should cause violations");
            assertTrue(hasViolationMessage(violations, BLANK_ERROR_MESSAGE),
                    "Should have blank name violation message");
        }

        @Test
        @DisplayName("Should fail when category name is too long")
        void shouldFailWhenCategoryNameIsTooLong() {
            String longName = "A".repeat(MAX_NAME_LENGTH + 1);
            Category category = createValidCategoryBuilder()
                    .categoryName(longName)
                    .build();

            Set<ConstraintViolation<Category>> violations = validateCategory(category);

            assertFalse(violations.isEmpty(), "Too long category name should cause violations");
            assertTrue(hasViolationMessage(violations, LENGTH_ERROR_MESSAGE),
                    "Should have length violation message");
        }
    }
}