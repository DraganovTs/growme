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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Delivery Method Entity Tests")
public class DeliveryMethodTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation for valid delivery method")
    void shouldPassValidationForValidDeliveryMethod() {
        DeliveryMethod deliveryMethod = DeliveryMethod.builder()
                .shortName("Express")
                .deliveryTime("1-2 days")
                .description("Fast delivery")
                .price(BigDecimal.valueOf(9.99))
                .build();

        Set<ConstraintViolation<DeliveryMethod>> violations = validator.validate(deliveryMethod);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when short name is null")
    void shouldFailValidationWhenShortNameIsNull() {
        DeliveryMethod deliveryMethod = DeliveryMethod.builder()
                .shortName(null)
                .deliveryTime("2-3 days")
                .description("Some description")
                .price(BigDecimal.valueOf(5.00))
                .build();

        Set<ConstraintViolation<DeliveryMethod>> violations = validator.validate(deliveryMethod);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shortName")));
    }

    @Test
    @DisplayName("Should fail validation when short name is too long")
    void shouldFailValidationWhenShortNameTooLong() {
        DeliveryMethod deliveryMethod = DeliveryMethod.builder()
                .shortName("A".repeat(31))
                .deliveryTime("2-3 days")
                .description("Too long name")
                .price(BigDecimal.valueOf(5.00))
                .build();

        Set<ConstraintViolation<DeliveryMethod>> violations = validator.validate(deliveryMethod);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shortName")));
    }

    @Test
    @DisplayName("Should fail validation when price is zero")
    void shouldFailValidationWhenPriceIsZero() {
        DeliveryMethod deliveryMethod = DeliveryMethod.builder()
                .shortName("Standard")
                .deliveryTime("3-5 days")
                .description("Free option")
                .price(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<DeliveryMethod>> violations = validator.validate(deliveryMethod);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation when delivery time is null")
    void shouldFailValidationWhenDeliveryTimeIsNull() {
        DeliveryMethod deliveryMethod = DeliveryMethod.builder()
                .shortName("Standard")
                .deliveryTime(null)
                .description("Missing delivery time")
                .price(BigDecimal.valueOf(4.50))
                .build();

        Set<ConstraintViolation<DeliveryMethod>> violations = validator.validate(deliveryMethod);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deliveryTime")));
    }
}
