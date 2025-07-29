package com.home.order.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("OrderResponseDTO Validation Tests")
public class OrderResponseDTOTests {
    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private OrderItemDTO item() {
        return OrderItemDTO.builder()
                .productId("prod123")
                .productName("Apple")
                .imageUrl("http://example.com/apple.jpg")
                .unitPrice(1.99)
                .quantity(2)
                .build();
    }

    private OrderResponseDTO validDto() {
        return OrderResponseDTO.builder()
                .orderId("order123")
                .orderDate(Instant.now())
                .deliveryMethodShortName("Express")
                .total(55.99)
                .status("PENDING")
                .subTotal(50.00)
                .shippingPrice(5.99)
                .orderItems(List.of(item()))
                .build();
    }

    @Test
    void shouldPassValidation() {
        var dto = validDto();
        Set<ConstraintViolation<OrderResponseDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenOrderIdIsBlank() {
        var dto = validDto();
        dto.setOrderId("  ");
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderId")));
    }

    @Test
    void shouldFailWhenOrderDateIsNull() {
        var dto = validDto();
        dto.setOrderDate(null);
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderDate")));
    }

    @Test
    void shouldFailWhenDeliveryMethodIsBlank() {
        var dto = validDto();
        dto.setDeliveryMethodShortName("");
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deliveryMethodShortName")));
    }

    @Test
    void shouldFailWhenTotalIsZero() {
        var dto = validDto();
        dto.setTotal(0.0);
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("total")));
    }

    @Test
    void shouldFailWhenStatusIsBlank() {
        var dto = validDto();
        dto.setStatus(" ");
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
    }

    @Test
    void shouldFailWhenOrderItemsIsEmpty() {
        var dto = validDto();
        dto.setOrderItems(List.of());
        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderItems")));
    }
}
