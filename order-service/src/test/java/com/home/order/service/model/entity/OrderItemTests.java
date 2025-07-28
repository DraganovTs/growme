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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Entity Tests")
public class OrderItemTests {
    private Validator validator;
    private ProductItemOrdered testProductItem;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        testProductItem = new ProductItemOrdered(
                UUID.randomUUID(),
                "Test Product",
                "https://example.com/image.jpg");
    }

    @Test
    @DisplayName("Should create valid OrderItem")
    void shouldCreateValidOrderItem() {
        OrderItem orderItem = new OrderItem(
                testProductItem,
                2,
                BigDecimal.valueOf(19.99));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail with null product item")
    void shouldFailWithNullProductItem() {
        OrderItem orderItem = new OrderItem(
                null,
                1,
                BigDecimal.TEN);

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("itemOrdered")));
    }

    @Test
    @DisplayName("Should fail with zero quantity")
    void shouldFailWithZeroQuantity() {
        OrderItem orderItem = new OrderItem(
                testProductItem,
                0,
                BigDecimal.valueOf(9.99));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    @DisplayName("Should fail with negative quantity")
    void shouldFailWithNegativeQuantity() {
        OrderItem orderItem = new OrderItem(
                testProductItem,
                -1,
                BigDecimal.valueOf(9.99));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    @DisplayName("Should fail with null price")
    void shouldFailWithNullPrice() {
        OrderItem orderItem = new OrderItem(
                testProductItem,
                1,
                null);

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail with negative price")
    void shouldFailWithNegativePrice() {
        OrderItem orderItem = new OrderItem(
                testProductItem,
                1,
                BigDecimal.valueOf(-0.01));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should generate ID on persist if null")
    void shouldGenerateIdOnPersistIfNull() {
        OrderItem orderItem = new OrderItem(
                testProductItem,
                1,
                BigDecimal.TEN);
        orderItem.setOrderItemId(null);

        orderItem.generateId();

        assertNotNull(orderItem.getOrderItemId());
    }

    @Test
    @DisplayName("Should keep existing ID on persist")
    void shouldKeepExistingIdOnPersist() {
        UUID existingId = UUID.randomUUID();
        OrderItem orderItem = new OrderItem(
                testProductItem,
                1,
                BigDecimal.TEN);
        orderItem.setOrderItemId(existingId);

        orderItem.generateId();

        assertEquals(existingId, orderItem.getOrderItemId());
    }
}
