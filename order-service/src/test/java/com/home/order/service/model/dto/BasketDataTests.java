package com.home.order.service.model.dto;

import com.home.order.service.model.entity.BasketItem;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BasketData Validation Tests")
public class BasketDataTests {
    private Validator validator;
    public final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private BasketData createValidBasket() {
        BasketItem item = new BasketItem(uuid, 2, "Apple", 1,
                "image.png", new BigDecimal(5), "tomato", "pink");
        BasketData basket = new BasketData();
        basket.setId("basket123");
        basket.setItems(List.of(item));
        basket.setDeliveryMethodId(1);
        basket.setShippingPrice(new BigDecimal("5.99"));
        basket.setClientSecret("secret");
        basket.setPaymentIntentId("intent_123");
        return basket;
    }

    @Test
    @DisplayName("Should pass validation for valid BasketData")
    void shouldPassValidation() {
        BasketData basket = createValidBasket();
        Set<ConstraintViolation<BasketData>> violations = validator.validate(basket);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when id is null")
    void shouldFailWhenIdIsNull() {
        BasketData basket = createValidBasket();
        basket.setId(null);

        Set<ConstraintViolation<BasketData>> violations = validator.validate(basket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    @DisplayName("Should fail when items list is null")
    void shouldFailWhenItemsIsNull() {
        BasketData basket = createValidBasket();
        basket.setItems(null);

        Set<ConstraintViolation<BasketData>> violations = validator.validate(basket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")));
    }


}
