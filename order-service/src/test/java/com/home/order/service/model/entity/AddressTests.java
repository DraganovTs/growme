package com.home.order.service.model.entity;

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
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Address buildValidAddress() {
        return new Address("John", "Doe", "123 Main Street", "Springfield", "IL", "62704");
    }

    @Test
    @DisplayName("Should pass validation for a valid address")
    void shouldPassValidationForValidAddress() {
        Address address = buildValidAddress();
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when firstName is blank")
    void shouldFailWhenFirstNameIsBlank() {
        Address address = new Address("", "Doe", "123 Main Street", "Springfield", "IL", "62704");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    @DisplayName("Should fail when lastName is blank")
    void shouldFailWhenLastNameIsBlank() {
        Address address = new Address("John", "", "123 Main Street", "Springfield", "IL", "62704");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
    }

    @Test
    @DisplayName("Should fail when street is too short")
    void shouldFailWhenStreetIsTooShort() {
        Address address = new Address("John", "Doe", "123", "Springfield", "IL", "62704");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("street")));
    }

    @Test
    @DisplayName("Should fail when city is blank")
    void shouldFailWhenCityIsBlank() {
        Address address = new Address("John", "Doe", "123 Main Street", "", "IL", "62704");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("city")));
    }

    @Test
    @DisplayName("Should fail when state is too long")
    void shouldFailWhenStateIsTooLong() {
        Address address = new Address("John", "Doe", "123 Main Street", "Springfield", "a".repeat(50), "62704");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should fail when zipCode is blank")
    void shouldFailWhenZipCodeIsBlank() {
        Address address = new Address("John", "Doe", "123 Main Street", "Springfield", "IL", "");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("zipCode")));
    }

    @Test
    @DisplayName("Should be equal when all fields match")
    void shouldBeEqualWhenAllFieldsMatch() {
        Address address1 = buildValidAddress();
        Address address2 = buildValidAddress();
        assertTrue(address1.equals(address2));
        assertTrue(address1.hashCode() == address2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when one field differs")
    void shouldNotBeEqualWhenFieldsDiffer() {
        Address address1 = buildValidAddress();
        Address address2 = new Address("Jane", "Doe", "123 Main Street", "Springfield", "IL", "62704");
        assertFalse(address1.equals(address2));
        assertFalse(address1.hashCode() == address2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when compared with null")
    void shouldNotBeEqualWhenComparedWithNull() {
        Address address = buildValidAddress();
        assertFalse(address.equals(null));
    }

    @Test
    @DisplayName("Should not be equal when compared with different class")
    void shouldNotBeEqualWhenComparedWithDifferentClass() {
        Address address = buildValidAddress();
        Object other = new Object();
        assertFalse(address.equals(other));
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        Address address = buildValidAddress();
        assertTrue(address.equals(address));
    }
}
