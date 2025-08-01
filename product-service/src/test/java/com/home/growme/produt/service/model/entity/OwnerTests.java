package com.home.growme.produt.service.model.entity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Owner Entity Tests")
public class OwnerTests {

    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "john@example.com";
    private static final String BLANK_ERROR_MESSAGE = "must not be blank";
    private static final String LENGTH_ERROR_MESSAGE = "must be at most 30 characters";
    private static final String EMAIL_ERROR_MESSAGE = "should be valid";
    private static final int MAX_LENGTH = 30;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Owner.OwnerBuilder createValidOwnerBuilder() {
        return Owner.builder()
                .ownerId(UUID.randomUUID())
                .ownerName(VALID_NAME)
                .ownerEmail(VALID_EMAIL);
    }

    private Set<ConstraintViolation<Owner>> validateOwner(Owner owner) {
        return validator.validate(owner);
    }

    private boolean hasViolationMessage(Set<ConstraintViolation<Owner>> violations, String message) {
        return violations.stream().anyMatch(v -> v.getMessage().contains(message));
    }

    @Nested
    @DisplayName("Valid Owner Tests")
    class ValidOwnerTests {
        @Test
        @DisplayName("Should pass with valid owner")
        void shouldPassWithValidOwner() {
            Owner owner = createValidOwnerBuilder().build();
            assertTrue(validateOwner(owner).isEmpty());
        }

        @Test
        @DisplayName("Should pass when owner ID is generated automatically")
        void shouldPassWhenOwnerIdIsGenerated() {
            Owner owner = Owner.builder()
                    .ownerName(VALID_NAME)
                    .ownerEmail(VALID_EMAIL)
                    .build();
            owner.generateId();

            Set<ConstraintViolation<Owner>> violations = validateOwner(owner);
            assertTrue(violations.isEmpty());
            assertNotNull(owner.getOwnerId());
        }

        @Test
        @DisplayName("Should pass with null ID before generation")
        void shouldPassWithNullIdBeforeGeneration() {
            Owner owner = Owner.builder()
                    .ownerName(VALID_NAME)
                    .ownerEmail(VALID_EMAIL)
                    .build();

            assertNull(owner.getOwnerId());
            assertTrue(validateOwner(owner).isEmpty());
        }
    }

    @Nested
    @DisplayName("Invalid Owner Tests")
    class InvalidOwnerTests {
        @Test
        @DisplayName("Should fail when owner name is blank")
        void shouldFailWhenOwnerNameIsBlank() {
            Owner owner = createValidOwnerBuilder()
                    .ownerName("")
                    .build();

            Set<ConstraintViolation<Owner>> violations = validateOwner(owner);
            assertFalse(violations.isEmpty());
            assertTrue(hasViolationMessage(violations, BLANK_ERROR_MESSAGE));
        }

        @Test
        @DisplayName("Should fail when owner name is too long")
        void shouldFailWhenOwnerNameIsTooLong() {
            Owner owner = createValidOwnerBuilder()
                    .ownerName("A".repeat(MAX_LENGTH + 1))
                    .build();

            Set<ConstraintViolation<Owner>> violations = validateOwner(owner);
            assertFalse(violations.isEmpty());
            assertTrue(hasViolationMessage(violations, LENGTH_ERROR_MESSAGE));
        }

        @Test
        @DisplayName("Should fail when email is blank")
        void shouldFailWhenEmailIsBlank() {
            Owner owner = createValidOwnerBuilder()
                    .ownerEmail("")
                    .build();

            Set<ConstraintViolation<Owner>> violations = validateOwner(owner);
            assertFalse(violations.isEmpty());
            assertTrue(hasViolationMessage(violations, BLANK_ERROR_MESSAGE));
        }

        @Test
        @DisplayName("Should fail when email is invalid")
        void shouldFailWhenEmailIsInvalid() {
            Owner owner = createValidOwnerBuilder()
                    .ownerEmail("invalid-email")
                    .build();

            Set<ConstraintViolation<Owner>> violations = validateOwner(owner);
            assertFalse(violations.isEmpty());
            assertTrue(hasViolationMessage(violations, EMAIL_ERROR_MESSAGE));
        }

        @Test
        @DisplayName("Should fail when email is too long")
        void shouldFailWhenEmailIsTooLong() {
            String longEmail = "a".repeat(25) + "@example.com";
            Owner owner = createValidOwnerBuilder()
                    .ownerEmail(longEmail)
                    .build();

            Set<ConstraintViolation<Owner>> violations = validateOwner(owner);
            assertFalse(violations.isEmpty());
            assertTrue(hasViolationMessage(violations, LENGTH_ERROR_MESSAGE));
        }
    }
}

