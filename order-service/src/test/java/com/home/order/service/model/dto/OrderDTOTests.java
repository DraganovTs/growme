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

@DisplayName("OrderDTO Validation Tests")
public class OrderDTOTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private OrderDTO createValidDto() {
        return OrderDTO.builder()
                .basketId("basket123")
                .deliveryMethodId(1)
                .shipToAddress(AddressDTO.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .street("Main St")
                        .city("City")
                        .state("State")
                        .zipCode("12345")
                        .build())
                .userEmail("buyer@example.com")
                .build();
    }

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidationWithValidDto() {
        var dto = createValidDto();
        Set<ConstraintViolation<OrderDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when basketId is blank")
    void shouldFailWhenBasketIdIsBlank() {
        var dto = createValidDto();
        dto.setBasketId("  ");

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("basketId")));
    }

    @Test
    @DisplayName("Should fail when deliveryMethodId is less than 1")
    void shouldFailWhenDeliveryMethodIdInvalid() {
        var dto = createValidDto();
        dto.setDeliveryMethodId(0);

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deliveryMethodId")));
    }

    @Test
    @DisplayName("Should fail when shipping address is null")
    void shouldFailWhenShipToAddressIsNull() {
        var dto = createValidDto();
        dto.setShipToAddress(null);

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shipToAddress")));
    }

    @Test
    @DisplayName("Should fail when userEmail is blank")
    void shouldFailWhenUserEmailIsBlank() {
        var dto = createValidDto();
        dto.setUserEmail(" ");

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userEmail")));
    }

    @Test
    @DisplayName("Should fail when userEmail is invalid format")
    void shouldFailWhenUserEmailIsInvalid() {
        var dto = createValidDto();
        dto.setUserEmail("invalid-email");

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userEmail")));
    }
}
