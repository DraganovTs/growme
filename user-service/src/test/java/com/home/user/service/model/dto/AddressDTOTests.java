package com.home.user.service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AddressDto Validation Tests")
public class AddressDTOTests {


    private Validator validator;
    private AddressDTO.AddressDTOBuilder validBuilder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validBuilder = AddressDTO.builder()
                .street("123 Main St")
                .city("San Francisco")
                .state("CA")
                .zipCode("94105");
    }

    @Test
    @DisplayName("Should validate a correct address")
    void shouldValidateCorrectAddress() {
        AddressDTO address = validBuilder.build();
        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Nested
    @DisplayName("Street Validation")
    class StreetValidation {

        @Test
        void shouldPassWhenStreetIsValid() {
            AddressDTO address = validBuilder.street("Baker Street 221B").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid street");
        }

        @Test
        void shouldFailWhenStreetIsBlank() {
            AddressDTO address = validBuilder.street("").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Street is required")));
        }

        @Test
        void shouldFailWhenStreetTooLong() {
            AddressDTO address = validBuilder.street("a".repeat(31)).build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Street cannot exceed")));
        }
    }

    @Nested
    @DisplayName("City Validation")
    class CityValidation {

        @Test
        void shouldPassWhenCityIsValid() {
            AddressDTO address = validBuilder.city("New York").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid city");
        }

        @Test
        void shouldFailWhenCityIsBlank() {
            AddressDTO address = validBuilder.city("").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("City is required")));
        }

        @Test
        void shouldFailWhenCityTooLong() {
            AddressDTO address = validBuilder.city("a".repeat(31)).build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("City cannot exceed")));
        }
    }

    @Nested
    @DisplayName("State Validation")
    class StateValidation {
        @Test
        void shouldPassWhenStateHasMinLength() {
            AddressDTO address = validBuilder.state("NY").build(); // Assuming min is 2
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for 2-char state");
        }

        @Test
        void shouldPassWhenStateHasMaxLength() {
            AddressDTO address = validBuilder.state("A".repeat(20)).build(); // Assuming max is 20
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for 20-char state");
        }

        @Test
        void shouldFailWhenStateIsBlank() {
            AddressDTO address = validBuilder.state("").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("State is required")));
        }

        @Test
        void shouldFailWhenStateTooShort() {
            AddressDTO address = validBuilder.state("A").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("State must be between")));
        }

        @Test
        void shouldFailWhenStateTooLong() {
            AddressDTO address = validBuilder.state("A".repeat(21)).build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("State must be between")));
        }
    }

    @Nested
    @DisplayName("Zip Code Validation")
    class ZipCodeValidation {
        @Test
        void shouldPassWhenZipCodeIsValid() {
            AddressDTO address = validBuilder.zipCode("94105").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid zip code");
        }

        @Test
        void shouldPassWhenZipCodeHasMinLength() {
            AddressDTO address = validBuilder.zipCode("94").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid zip code");
        }

        @Test
        void shouldPassWhenZipCodeHasMaxLength() {
            AddressDTO address = validBuilder.zipCode("12345678").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid zip code");
        }

        @Test
        void shouldFailWhenZipCodeIsBlank() {
            AddressDTO address = validBuilder.zipCode("").build();
            Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Zip code is required")));
        }
    }

}
