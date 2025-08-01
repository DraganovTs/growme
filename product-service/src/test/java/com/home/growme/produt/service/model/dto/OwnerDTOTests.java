package com.home.growme.produt.service.model.dto;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OwnerDTO Validation Tests")public class OwnerDTOTests {
    private static final String VALID_OWNER_NAME = "John Doe";
    private static final String BLANK_MESSAGE = "must not be blank";
    private static final String LENGTH_MESSAGE = "must be at most 30 characters";
    private static final int MAX_NAME_LENGTH = 30;
    private static final String VALID_UUID = "d290f1ee-6c54-4b01-90e6-d701748f0851";

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private OwnerDTO.OwnerDTOBuilder createValidOwnerBuilder() {
        return OwnerDTO.builder().ownerName(VALID_OWNER_NAME);
    }

    private boolean hasValidationMessage(Set<ConstraintViolation<OwnerDTO>> violations, String message) {
        return violations.stream().anyMatch(v -> v.getMessage().contains(message));
    }

    @Nested
    @DisplayName("Valid Owner Tests")
    class ValidOwnerTests {
        @Test
        @DisplayName("Should pass with valid OwnerDTO")
        void shouldPassWithValidOwnerDTO() {
            OwnerDTO dto = createValidOwnerBuilder()
                    .ownerId(UUID.randomUUID())
                    .build();
            assertTrue(validator.validate(dto).isEmpty());
        }

        @Test
        @DisplayName("Should pass when ownerId is null")
        void shouldPassWhenOwnerIdIsNull() {
            OwnerDTO dto = createValidOwnerBuilder().build();
            assertTrue(validator.validate(dto).isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"John@Doe", "John_Doe", "John+Doe"})
        @DisplayName("Should pass with special characters in ownerName")
        void shouldPassWithSpecialCharactersInOwnerName(String name) {
            OwnerDTO dto = createValidOwnerBuilder()
                    .ownerName(name)
                    .build();
            assertTrue(validator.validate(dto).isEmpty());
        }

        @Test
        @DisplayName("Should pass with boundary length names")
        void shouldPassWithBoundaryLengthNames() {
            assertTrue(validator.validate(createValidOwnerBuilder()
                    .ownerName("J").build()).isEmpty());
            assertTrue(validator.validate(createValidOwnerBuilder()
                    .ownerName("A".repeat(MAX_NAME_LENGTH)).build()).isEmpty());
        }
    }

    @Nested
    @DisplayName("Invalid Owner Tests")
    class InvalidOwnerTests {
        @Test
        @DisplayName("Should fail when ownerName is blank")
        void shouldFailWhenOwnerNameIsBlank() {
            OwnerDTO dto = createValidOwnerBuilder().ownerName("").build();
            Set<ConstraintViolation<OwnerDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(hasValidationMessage(violations, BLANK_MESSAGE));
        }

        @Test
        @DisplayName("Should fail when ownerName is null")
        void shouldFailWhenOwnerNameIsNull() {
            OwnerDTO dto = createValidOwnerBuilder().ownerName(null).build();
            Set<ConstraintViolation<OwnerDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(hasValidationMessage(violations, BLANK_MESSAGE));
        }

        @Test
        @DisplayName("Should fail when ownerName is too long")
        void shouldFailWhenOwnerNameIsTooLong() {
            OwnerDTO dto = createValidOwnerBuilder()
                    .ownerName("A".repeat(MAX_NAME_LENGTH + 1))
                    .build();
            Set<ConstraintViolation<OwnerDTO>> violations = validator.validate(dto);
            assertFalse(violations.isEmpty());
            assertTrue(hasValidationMessage(violations, LENGTH_MESSAGE));
        }
    }

    @Nested
    @DisplayName("Object Contract Tests")
    class ObjectContractTests {
        @Test
        @DisplayName("Should implement proper equals and hashCode")
        void shouldImplementProperEqualsAndHashCode() {
            UUID id = UUID.randomUUID();
            OwnerDTO dto1 = createValidOwnerBuilder().ownerId(id).build();
            OwnerDTO dto2 = createValidOwnerBuilder().ownerId(id).build();
            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should have correct toString implementation")
        void shouldHaveCorrectToStringImplementation() {
            UUID id = UUID.fromString(VALID_UUID);
            OwnerDTO dto = createValidOwnerBuilder().ownerId(id).build();
            String expectedString = "OwnerDTO(ownerId=" + VALID_UUID + ", ownerName=" + VALID_OWNER_NAME + ")";
            assertEquals(expectedString, dto.toString());
        }
    }
}