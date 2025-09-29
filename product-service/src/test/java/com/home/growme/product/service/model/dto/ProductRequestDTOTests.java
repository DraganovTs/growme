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

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductRequestDTO Validation Tests")
class ProductRequestDTOTests {
    private static final int MAX_NAME_LENGTH = 100;
    private static final String VALID_NAME = "Organic Apple";
    private static final String VALID_DESCRIPTION = "Fresh organic apples";
    private static final BigDecimal VALID_PRICE = new BigDecimal("1.00");

    private Validator validator;
    private UUID testOwnerId;
    private String testCategoryName;
    private ProductRequestDTO.ProductRequestDTOBuilder validDtoBuilder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        testOwnerId = UUID.randomUUID();
        testCategoryName = "Fruits";

        validDtoBuilder = ProductRequestDTO.builder()
                .name(VALID_NAME)
                .description(VALID_DESCRIPTION)
                .price(VALID_PRICE)
                .categoryName(testCategoryName)
                .ownerId(testOwnerId);
    }

    @Test
    @DisplayName("Should pass validation with all valid fields")
    void shouldPassWithValidProductRequestDTO() {
        ProductRequestDTO dto = createValidDto();
        assertValidationPasses(dto);
    }

    @Nested
    @DisplayName("Name validation")
    class NameValidation {
        @Test
        @DisplayName("Should fail when blank")
        void shouldFailWhenNameIsBlank() {
            ProductRequestDTO dto = validDtoBuilder.name("").build();
            assertValidationFailsWithMessage(dto, "Product name is required");
        }

        @Test
        @DisplayName("Should fail when exceeds maximum length")
        void shouldFailWhenNameIsTooLong() {
            ProductRequestDTO dto = validDtoBuilder.name("A".repeat(MAX_NAME_LENGTH + 1)).build();
            assertValidationFailsWithMessage(dto, "must be less than 100 characters");
        }
    }

    @Nested
    @DisplayName("Price validation")
    class PriceValidation {
        @ParameterizedTest
        @ValueSource(strings = {"0.00", "-1.00"})
        @DisplayName("Should fail when not positive")
        void shouldFailWhenPriceIsNotPositive(String priceValue) {
            ProductRequestDTO dto = validDtoBuilder.price(new BigDecimal(priceValue)).build();
            assertValidationFailsWithMessage(dto, "must be greater than 0");
        }

        @Test
        @DisplayName("Should fail when null")
        void shouldFailWhenPriceIsNull() {
            ProductRequestDTO dto = validDtoBuilder.price(null).build();
            assertValidationFailsWithMessage(dto, "Price is required");
        }
    }

    @Test
    @DisplayName("Should pass with minimum valid values")
    void shouldPassWithMinimumValidValues() {
        ProductRequestDTO dto = ProductRequestDTO.builder()
                .name("A")
                .description("B")
                .price(new BigDecimal("0.01"))
                .unitsInStock(0)
                .categoryName("C")
                .ownerId(testOwnerId)
                .build();
        assertValidationPasses(dto);
    }

    private ProductRequestDTO createValidDto() {
        return validDtoBuilder.build();
    }

    private void assertValidationPasses(ProductRequestDTO dto) {
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected validation to pass but found violations: " + violations);
    }

    private void assertValidationFailsWithMessage(ProductRequestDTO dto, String expectedMessage) {
        Set<ConstraintViolation<ProductRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected validation to fail");
        assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().contains(expectedMessage)),
                "Expected violation message containing: " + expectedMessage);
    }
}