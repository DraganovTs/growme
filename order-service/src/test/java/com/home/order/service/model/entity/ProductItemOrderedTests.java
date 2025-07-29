package com.home.order.service.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Product Item Ordered Entity Tests")
public class ProductItemOrderedTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ProductItemOrdered createValidProductItemOrdered() {
        return new ProductItemOrdered(
                UUID.randomUUID(),
                "Green Apple",
                "http://example.com/apple.jpg"
        );
    }

    @Test
    @DisplayName("Should pass validation for valid ProductItemOrdered")
    void shouldPassValidationForValidProductItemOrdered() {
        ProductItemOrdered product = createValidProductItemOrdered();
        Set<ConstraintViolation<ProductItemOrdered>> violations = validator.validate(product);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when productItemId is null")
    void shouldFailWhenProductItemIdIsNull() {
        ProductItemOrdered product = new ProductItemOrdered(
                null,
                "Green Apple",
                "http://example.com/apple.jpg"
        );
        Set<ConstraintViolation<ProductItemOrdered>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productItemId")));
    }

    @Test
    @DisplayName("Should fail validation when productName is null")
    void shouldFailWhenProductNameIsNull() {
        ProductItemOrdered product = new ProductItemOrdered(
                UUID.randomUUID(),
                null,
                "http://example.com/apple.jpg"
        );
        Set<ConstraintViolation<ProductItemOrdered>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productName")));
    }

    @Test
    @DisplayName("Should fail validation when productName exceeds max length")
    void shouldFailWhenProductNameTooLong() {
        ProductItemOrdered product = new ProductItemOrdered(
                UUID.randomUUID(),
                "A".repeat(31),
                "http://example.com/apple.jpg"
        );
        Set<ConstraintViolation<ProductItemOrdered>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productName")));
    }

    @Test
    @DisplayName("Should fail validation when imageUrl is null")
    void shouldFailWhenImageUrlIsNull() {
        ProductItemOrdered product = new ProductItemOrdered(
                UUID.randomUUID(),
                "Green Apple",
                null
        );
        Set<ConstraintViolation<ProductItemOrdered>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("imageUrl")));
    }

    @Test
    @DisplayName("Should fail validation when imageUrl exceeds max length")
    void shouldFailWhenImageUrlTooLong() {
        ProductItemOrdered product = new ProductItemOrdered(
                UUID.randomUUID(),
                "Green Apple",
                "http://example.com/" + "a".repeat(245)
        );
        Set<ConstraintViolation<ProductItemOrdered>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("imageUrl")));
    }

    @Test
    @DisplayName("Should be equal when all fields match")
    void shouldBeEqualWhenAllFieldsMatch() {
        UUID id = UUID.randomUUID();
        ProductItemOrdered item1 = new ProductItemOrdered(id, "Green Apple", "http://example.com/apple.jpg");
        ProductItemOrdered item2 = new ProductItemOrdered(id, "Green Apple", "http://example.com/apple.jpg");

        assertTrue(item1.equals(item2));
        assertTrue(item1.hashCode() == item2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when one field differs")
    void shouldNotBeEqualWhenFieldsDiffer() {
        UUID id = UUID.randomUUID();
        ProductItemOrdered item1 = new ProductItemOrdered(id, "Green Apple", "http://example.com/apple.jpg");
        ProductItemOrdered item2 = new ProductItemOrdered(id, "Red Apple", "http://example.com/apple.jpg");

        assertFalse(item1.equals(item2));
        assertFalse(item1.hashCode() == item2.hashCode());
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        ProductItemOrdered item = createValidProductItemOrdered();
        assertTrue(item.equals(item));
    }

    @Test
    @DisplayName("Should not be equal when compared with null")
    void shouldNotBeEqualToNull() {
        ProductItemOrdered item = createValidProductItemOrdered();
        assertFalse(item.equals(null));
    }

    @Test
    @DisplayName("Should not be equal when compared to different class")
    void shouldNotBeEqualToDifferentClass() {
        ProductItemOrdered item = createValidProductItemOrdered();
        Object other = new Object();
        assertFalse(item.equals(other));
    }
}
