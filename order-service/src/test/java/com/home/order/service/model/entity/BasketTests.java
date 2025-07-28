package com.home.order.service.model.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Basket Entity Tests")
public class BasketTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Basket createValidBasket() {
        Basket basket = new Basket(UUID.randomUUID().toString());
        basket.setItems(Collections.singletonList(
                new BasketItem(
                        UUID.randomUUID(),
                        2,
                        "Apple",
                        20,
                        "http://example.com/apple.jpg",
                        BigDecimal.valueOf(2.49),
                        "FreshBrand",
                        "Fruits"
                )
        ));
        basket.setDeliveryMethodId(1);
        basket.setShippingPrice(BigDecimal.valueOf(4.99));
        basket.setClientSecret("secret123");
        basket.setPaymentIntentId("intent456");
        return basket;
    }

    @Test
    @DisplayName("Should pass validation for valid basket")
    void shouldPassValidationForValidBasket() {
        Basket basket = createValidBasket();

        Set<ConstraintViolation<Basket>> violations = validator.validate(basket);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when basket ID is null")
    void shouldFailValidationWhenBasketIdIsNull() {
        Basket basket = createValidBasket();
        basket.setId(null);

        Set<ConstraintViolation<Basket>> violations = validator.validate(basket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    @DisplayName("Should fail validation when basket ID is too long")
    void shouldFailValidationWhenBasketIdTooLong() {
        Basket basket = createValidBasket();
        basket.setId("a".repeat(37));

        Set<ConstraintViolation<Basket>> violations = validator.validate(basket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    @DisplayName("Should fail validation when items list is null")
    void shouldFailValidationWhenItemsIsNull() {
        Basket basket = createValidBasket();
        basket.setItems(null);

        Set<ConstraintViolation<Basket>> violations = validator.validate(basket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")));
    }

    @Test
    @DisplayName("Should fail validation when items list is empty")
    void shouldFailValidationWhenItemsIsEmpty() {
        Basket basket = createValidBasket();
        basket.setItems(Collections.emptyList());

        Set<ConstraintViolation<Basket>> violations = validator.validate(basket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")));
    }

    @Test
    @DisplayName("Should fail validation when shipping price is negative")
    void shouldFailValidationWhenShippingPriceIsNegative() {
        Basket basket = createValidBasket();
        basket.setShippingPrice(BigDecimal.valueOf(-1));

        Set<ConstraintViolation<Basket>> violations = validator.validate(basket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shippingPrice")));
    }
}
