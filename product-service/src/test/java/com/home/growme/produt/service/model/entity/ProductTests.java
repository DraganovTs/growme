package com.home.growme.produt.service.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTests {
    private static final String VALID_NAME = "Valid Name";
    private static final String VALID_DESCRIPTION = "Valid description";
    private static final BigDecimal VALID_PRICE = new BigDecimal("10.00");
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    private Validator validator;
    private Category testCategory;
    private Owner testOwner;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        testCategory = Category.builder()
                .categoryId(UUID.randomUUID())
                .categoryName("Electronics")
                .build();
        testOwner = Owner.builder()
                .ownerId(UUID.randomUUID())
                .ownerName("John Doe")
                .ownerEmail("john@example.com")
                .build();
    }

    private Product.ProductBuilder validProductBuilder() {
        return Product.builder()
                .name(VALID_NAME)
                .description(VALID_DESCRIPTION)
                .price(VALID_PRICE)
                .category(testCategory);
    }

    private void assertValidationMessage(Product product, String expectedMessage) {
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains(expectedMessage)));
    }

    @Nested
    @DisplayName("Valid Product Tests")
    class ValidProductTests {
        @Test
        @DisplayName("Should pass with complete valid product")
        void shouldPassWithValidProduct() {
            Product product = Product.builder()
                    .productId(UUID.randomUUID())
                    .name("Smartphone")
                    .brand("TechBrand")
                    .description("Latest model with advanced features")
                    .price(new BigDecimal("599.99"))
                    .unitsInStock(100)
                    .imageUrl("http://example.com/image.jpg")
                    .category(testCategory)
                    .owner(testOwner)
                    .build();

            assertTrue(validator.validate(product).isEmpty());
        }

        @Test
        @DisplayName("Should pass when owner is null")
        void shouldPassWithNullOwner() {
            Product product = validProductBuilder().owner(null).build();
            assertTrue(validator.validate(product).isEmpty());
        }
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {
        @Test
        @DisplayName("Should fail when name is blank")
        void shouldFailWithBlankName() {
            Product product = validProductBuilder().name("").build();
            assertValidationMessage(product, "must not be blank");
        }

        @Test
        @DisplayName("Should fail when name exceeds length limit")
        void shouldFailWithLongName() {
            Product product = validProductBuilder()
                    .name("A".repeat(MAX_NAME_LENGTH + 1))
                    .build();
            assertValidationMessage(product, "must be at most 30 characters");
        }
    }

    @Nested
    @DisplayName("Stock Management Tests")
    class StockManagementTests {
        @Test
        @DisplayName("Should reduce stock successfully")
        void shouldReduceStock() {
            Product product = validProductBuilder()
                    .unitsInStock(10)
                    .build();
            product.reduceStock(5);
            assertEquals(5, product.getUnitsInStock());
        }

        @Test
        @DisplayName("Should throw on negative reduction quantity")
        void shouldThrowOnNegativeReduction() {
            Product product = validProductBuilder()
                    .unitsInStock(10)
                    .build();
            assertThrows(IllegalArgumentException.class,
                    () -> product.reduceStock(-1));
        }

        @Test
        @DisplayName("Should throw on insufficient stock")
        void shouldThrowOnInsufficientStock() {
            Product product = validProductBuilder()
                    .unitsInStock(2)
                    .build();
            assertThrows(IllegalStateException.class,
                    () -> product.reduceStock(5));
        }
    }
}