package com.home.user.service.model.entity;

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

@DisplayName("Address Entity Tests")
public class AddressTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass with valid address")
    void shouldPassWithValidAddress() {
        Address address = Address.builder()
                .street("123 Main St")
                .city("Springfield")
                .state("Illinois")
                .zipCode("1234")
                .build();

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when street is too long")
    void shouldFailWhenStreetIsTooLong() {
        String longStreet = "A".repeat(101);
        Address address = Address.builder()
                .street(longStreet)
                .city("City")
                .state("State")
                .zipCode("1234")
                .build();

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Street name cannot exceed")));
    }

    @Test
    @DisplayName("Should fail when city is too long")
    void shouldFailWhenCityIsTooLong() {
        Address address = Address.builder()
                .street("Street")
                .city("C".repeat(51))
                .state("State")
                .zipCode("1234")
                .build();

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("City name cannot exceed")));
    }

    @Test
    @DisplayName("Should fail when state is too long")
    void shouldFailWhenStateIsTooLong() {
        Address address = Address.builder()
                .street("Street")
                .city("City")
                .state("S".repeat(51))
                .zipCode("1234")
                .build();

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("State name cannot exceed")));
    }

    @Test
    @DisplayName("Should fail with invalid zip code")
    void shouldFailWithInvalidZipCode() {
        Address address = Address.builder()
                .street("Street")
                .city("City")
                .state("State")
                .zipCode("12345")
                .build();

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid zip code format")));
    }

    @Test
    @DisplayName("Should fail when zip code contains letters")
    void shouldFailWhenZipCodeContainsLetters() {
        Address address = Address.builder()
                .street("Street")
                .city("City")
                .state("State")
                .zipCode("12AB")
                .build();

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid zip code format")));
    }
}
