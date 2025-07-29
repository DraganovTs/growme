package com.home.order.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DeliveryMethodDTO Validation Tests")
public class DeliveryMethodDTOTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private DeliveryMethodDTO createValidDto() {
        return DeliveryMethodDTO.builder()
                .deliveryMethodId(1)
                .shortName("Express")
                .deliveryTime("1-2 business days")
                .description("Fast delivery with tracking")
                .price(new BigDecimal("9.99"))
                .build();
    }

    @Test
    @DisplayName("Should pass validation for valid DTO")
    void shouldPassValidation() {
        var dto = createValidDto();
        Set<ConstraintViolation<DeliveryMethodDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when deliveryMethodId is null")
    void shouldFailWhenDeliveryMethodIdIsNull() {
        var dto = createValidDto();
        dto.setDeliveryMethodId(null);

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deliveryMethodId")));
    }

    @Test
    @DisplayName("Should fail when shortName is blank")
    void shouldFailWhenShortNameIsBlank() {
        var dto = createValidDto();
        dto.setShortName(" ");

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shortName")));
    }

    @Test
    @DisplayName("Should fail when deliveryTime is null")
    void shouldFailWhenDeliveryTimeIsNull() {
        var dto = createValidDto();
        dto.setDeliveryTime(null);

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("deliveryTime")));
    }

    @Test
    @DisplayName("Should fail when description exceeds max length")
    void shouldFailWhenDescriptionTooLong() {
        var dto = createValidDto();
        dto.setDescription("A".repeat(101));

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    @DisplayName("Should fail when price is zero or negative")
    void shouldFailWhenPriceIsInvalid() {
        var dto = createValidDto();
        dto.setPrice(BigDecimal.ZERO);

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }
}
