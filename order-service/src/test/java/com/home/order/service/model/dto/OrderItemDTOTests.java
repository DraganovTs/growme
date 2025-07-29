package com.home.order.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("OrderItemDTO Validation Tests")
public class OrderItemDTOTests {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private OrderItemDTO validDto() {
        return OrderItemDTO.builder()
                .productId("prod123")
                .productName("Apple")
                .imageUrl("http://example.com/apple.jpg")
                .unitPrice(1.99)
                .quantity(3)
                .build();
    }

    @Test
    void shouldPassWithValidData() {
        var dto = validDto();
        Set<ConstraintViolation<OrderItemDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenProductIdIsBlank() {
        var dto = validDto();
        dto.setProductId("  ");
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productId")));
    }

    @Test
    void shouldFailWhenProductNameIsBlank() {
        var dto = validDto();
        dto.setProductName("");
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productName")));
    }

    @Test
    void shouldFailWhenImageUrlIsBlank() {
        var dto = validDto();
        dto.setImageUrl(" ");
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("imageUrl")));
    }

    @Test
    void shouldFailWhenUnitPriceIsZero() {
        var dto = validDto();
        dto.setUnitPrice(0.0);
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("unitPrice")));
    }

    @Test
    void shouldFailWhenQuantityIsNull() {
        var dto = validDto();
        dto.setQuantity(null);
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    void shouldFailWhenQuantityIsLessThanOne() {
        var dto = validDto();
        dto.setQuantity(0);
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }
}
