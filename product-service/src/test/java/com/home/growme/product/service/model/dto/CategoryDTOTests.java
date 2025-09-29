package com.home.growme.product.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryDTO Validation Tests")
public class CategoryDTOTests {
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 10;
    private static final String VALID_CATEGORY_NAME = "Fruits";
    private static final String SIZE_VALIDATION_MESSAGE = "size must be between 3 and 10";
    private static final String NAME_REQUIRED_MESSAGE = "Category name is required";

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CategoryDTO.CategoryDTOBuilder createValidCategoryBuilder() {
        return CategoryDTO.builder().categoryName(VALID_CATEGORY_NAME);
    }

    private boolean hasValidationMessage(Set<ConstraintViolation<CategoryDTO>> violations, String message) {
        return violations.stream().anyMatch(v -> v.getMessage().contains(message));
    }

    @Nested
    @DisplayName("Valid Category Tests")
    class ValidCategoryTests {
        @Test
        @DisplayName("Should pass with valid CategoryDTO")
        void shouldPassWithValidCategory() {
            CategoryDTO dto = createValidCategoryBuilder()
                    .categoryId(UUID.randomUUID())
                    .build();
            assertTrue(validator.validate(dto).isEmpty());
        }

        @Test
        @DisplayName("Should pass with null categoryId")
        void shouldPassWithNullCategoryId() {
            CategoryDTO dto = createValidCategoryBuilder().build();
            assertTrue(validator.validate(dto).isEmpty());
        }

        @Test
        @DisplayName("Should pass with boundary length names")
        void shouldPassWithBoundaryLengthNames() {
            assertTrue(validator.validate(createValidCategoryBuilder()
                    .categoryName("ABC").build()).isEmpty());
            assertTrue(validator.validate(createValidCategoryBuilder()
                    .categoryName("A".repeat(MAX_NAME_LENGTH)).build()).isEmpty());
        }
    }

    @Nested
    @DisplayName("Invalid Category Tests")
    class InvalidCategoryTests {
        @Test
        @DisplayName("Should fail with blank name")
        void shouldFailWithBlankName() {
            CategoryDTO dto = createValidCategoryBuilder().categoryName("").build();
            Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(hasValidationMessage(violations, NAME_REQUIRED_MESSAGE));
        }

        @Test
        @DisplayName("Should fail with null name")
        void shouldFailWithNullName() {
            CategoryDTO dto = createValidCategoryBuilder().categoryName(null).build();
            Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(hasValidationMessage(violations, NAME_REQUIRED_MESSAGE));
        }

        @ParameterizedTest
        @ValueSource(strings = {"AB", "A"})
        @DisplayName("Should fail with too short name")
        void shouldFailWithTooShortName(String name) {
            CategoryDTO dto = createValidCategoryBuilder().categoryName(name).build();
            Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(hasValidationMessage(violations, SIZE_VALIDATION_MESSAGE));
        }

        @Test
        @DisplayName("Should fail with too long name")
        void shouldFailWithTooLongName() {
            CategoryDTO dto = createValidCategoryBuilder()
                    .categoryName("A".repeat(MAX_NAME_LENGTH + 1))
                    .build();
            Set<ConstraintViolation<CategoryDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(hasValidationMessage(violations, SIZE_VALIDATION_MESSAGE));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Electronics!", "Elect@ronics", "Elect#ronics"})
        @DisplayName("Should fail with special characters")
        void shouldFailWithSpecialCharacters(String name) {
            CategoryDTO dto = createValidCategoryBuilder().categoryName(name).build();
            assertFalse(validator.validate(dto).isEmpty());
        }
    }
}

