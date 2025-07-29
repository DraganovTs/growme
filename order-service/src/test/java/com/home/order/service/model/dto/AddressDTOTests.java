package com.home.order.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AddressDTO Validation Tests")
public class AddressDTOTests {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private AddressDTO createValidAddress() {
        return AddressDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .street("123 Main St")
                .city("San Francisco")
                .state("CA")
                .zipCode("94105")
                .build();
    }

    @Test
    @DisplayName("Should pass validation for valid AddressDTO")
    void shouldPassValidation() {
        AddressDTO address = createValidAddress();
        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when firstName is blank")
    void shouldFailWhenFirstNameIsBlank() {
        AddressDTO address = createValidAddress();
        address.setFirstName("  ");

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    @DisplayName("Should fail when lastName exceeds max length")
    void shouldFailWhenLastNameTooLong() {
        AddressDTO address = createValidAddress();
        address.setLastName("A".repeat(31));

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
    }

    @Test
    @DisplayName("Should fail when street is null")
    void shouldFailWhenStreetIsNull() {
        AddressDTO address = createValidAddress();
        address.setStreet(null);

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("street")));
    }

    @Test
    @DisplayName("Should fail when city is blank")
    void shouldFailWhenCityIsBlank() {
        AddressDTO address = createValidAddress();
        address.setCity("");

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("city")));
    }

    @Test
    @DisplayName("Should fail when state is too short")
    void shouldFailWhenStateTooShort() {
        AddressDTO address = createValidAddress();
        address.setState("A"); // min = 2

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should fail when state is too long")
    void shouldFailWhenStateTooLong() {
        AddressDTO address = createValidAddress();
        address.setState("A".repeat(21));

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should fail when zipCode is blank")
    void shouldFailWhenZipCodeIsBlank() {
        AddressDTO address = createValidAddress();
        address.setZipCode(" ");

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("zipCode")));
    }
}
