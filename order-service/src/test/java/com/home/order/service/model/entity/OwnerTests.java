package com.home.order.service.model.entity;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Owner Entity Tests")
public class OwnerTests {

    private Validator validator;
    private UUID testOwnerId;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        testOwnerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should pass with valid owner")
    void shouldPassWithValidOwner() {
        Owner owner = Owner.builder()
                .ownerId(testOwnerId)
                .ownerName("John Doe")
                .ownerEmail("john.doe@example.com")
                .build();

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail when owner name is too long")
    void shouldFailWhenOwnerNameIsTooLong() {
        String longName = "A".repeat(31);
        Owner owner = Owner.builder()
                .ownerId(testOwnerId)
                .ownerName(longName)
                .ownerEmail("email@example.com")
                .build();

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Owner name must not exceed")));
    }

    @Test
    @DisplayName("Should fail when owner email is too long")
    void shouldFailWhenOwnerEmailIsTooLong() {
        String longEmail = "a".repeat(25) + "@" + "b".repeat(6) + ".com";
        Owner owner = Owner.builder()
                .ownerId(testOwnerId)
                .ownerName("Valid Name")
                .ownerEmail(longEmail)
                .build();

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Owner email must not exceed")));
    }

    @Test
    @DisplayName("Should fail when owner email is invalid")
    void shouldFailWhenOwnerEmailIsInvalid() {
        Owner owner = Owner.builder()
                .ownerId(testOwnerId)
                .ownerName("Valid Name")
                .ownerEmail("invalid-email")
                .build();

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("ownerEmail")));
    }

    @Test
    @DisplayName("Should pass with null owner email")
    void shouldPassWithNullOwnerEmail() {
        Owner owner = Owner.builder()
                .ownerId(testOwnerId)
                .ownerName("Valid Name")
                .ownerEmail(null)
                .build();

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail with null owner name")
    void shouldFailWithNullOwnerName() {
        Owner owner = Owner.builder()
                .ownerId(testOwnerId)
                .ownerName(null)
                .ownerEmail("email@example.com")
                .build();

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("ownerName")));
    }

    @Test
    @DisplayName("Should fail with null owner ID")
    void shouldFailWithNullOwnerId() {
        Owner owner = Owner.builder()
                .ownerId(null)
                .ownerName("Valid Name")
                .ownerEmail("email@example.com")
                .build();

        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("ownerId")));
    }
}
