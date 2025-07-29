package com.home.order.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("IOrderDto Validation Tests")
public class IOrderDtoTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private IOrderDto createValidDto() {
        return IOrderDto.builder()
                .orderId("order123")
                .buyerEmail("buyer@example.com")
                .orderDate(LocalDateTime.now())
                .shipToAddress(AddressDTO.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .street("123 Main St")
                        .city("New York")
                        .state("NY")
                        .zipCode("12345")
                        .build())
                .deliveryMethod("Express")
                .shippingPrice(new BigDecimal("5.99"))
                .orderItems(List.of(new OrderItemDTO("product1", "Item A", "img.png", 10.00, 2)))
                .subTotal(new BigDecimal("20.00"))
                .total(new BigDecimal("25.99"))
                .status("PENDING")
                .build();
    }

    @Test
    @DisplayName("Should pass validation with valid IOrderDto")
    void shouldPassWithValidDto() {
        var dto = createValidDto();
        Set<ConstraintViolation<IOrderDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when buyerEmail is invalid")
    void shouldFailWithInvalidEmail() {
        var dto = createValidDto();
        dto.setBuyerEmail("invalid-email");

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("buyerEmail")));
    }

    @Test
    @DisplayName("Should fail when shipping price is zero")
    void shouldFailWhenShippingPriceZero() {
        var dto = createValidDto();
        dto.setShippingPrice(BigDecimal.ZERO);

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shippingPrice")));
    }

    @Test
    @DisplayName("Should fail when orderItems is empty")
    void shouldFailWhenOrderItemsIsEmpty() {
        var dto = createValidDto();
        dto.setOrderItems(List.of());

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderItems")));
    }

    @Test
    @DisplayName("Should fail when shipToAddress is null")
    void shouldFailWhenShipToAddressNull() {
        var dto = createValidDto();
        dto.setShipToAddress(null);

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shipToAddress")));
    }

    @Test
    @DisplayName("Should fail when orderId is blank")
    void shouldFailWhenOrderIdBlank() {
        var dto = createValidDto();
        dto.setOrderId("  ");

        var violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("orderId")));
    }
}
