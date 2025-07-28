package com.home.order.service.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Basket Item Entity Tests")
public class BasketItemTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private BasketItem createValidBasketItem() {
        return new BasketItem(
                UUID.randomUUID(),
                2,
                "Apple",
                10,
                "http://example.com/apple.jpg",
                BigDecimal.valueOf(1.99),
                "FreshFruits",
                "Fruits"
        );
    }

    @Test
    @DisplayName("Should pass validation for valid basket item")
    void shouldPassValidationForValidBasketItem() {
        BasketItem item = createValidBasketItem();

        Set<ConstraintViolation<BasketItem>> violations = validator.validate(item);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when quantity is less than 1")
    void shouldFailValidationWhenQuantityIsLessThanOne() {
        BasketItem item = createValidBasketItem();
        item.setQuantity(0);

        Set<ConstraintViolation<BasketItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    @DisplayName("Should fail validation when product name is too long")
    void shouldFailValidationWhenProductNameTooLong() {
        BasketItem item = createValidBasketItem();
        item.setName("A".repeat(31));

        Set<ConstraintViolation<BasketItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation when price is zero")
    void shouldFailValidationWhenPriceIsZero() {
        BasketItem item = createValidBasketItem();
        item.setPrice(BigDecimal.ZERO);

        Set<ConstraintViolation<BasketItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation when brand name is null")
    void shouldFailValidationWhenBrandNameIsNull() {
        BasketItem item = createValidBasketItem();
        item.setBrandName(null);

        Set<ConstraintViolation<BasketItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("brandName")));
    }

    @Test
    @DisplayName("Should fail validation when image URL exceeds 255 characters")
    void shouldFailValidationWhenImageUrlTooLong() {
        BasketItem item = createValidBasketItem();
        item.setImageUrl("http://example.com/" + "a".repeat(240) + ".jpg");

        Set<ConstraintViolation<BasketItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("imageUrl")));
    }
}
