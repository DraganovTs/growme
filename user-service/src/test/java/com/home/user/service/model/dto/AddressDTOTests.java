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
    private AddressDto.AddressDtoBuilder validBuilder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validBuilder = AddressDto.builder()
                .street("123 Main St")
                .city("San Francisco")
                .state("CA")
                .zipCode("94105");
    }

    @Test
    @DisplayName("Should validate a correct address")
    void shouldValidateCorrectAddress() {
        AddressDto address = validBuilder.build();
        Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Nested
    @DisplayName("Street Validation")
    class StreetValidation {

        @Test
        void shouldPassWhenStreetIsValid() {
            AddressDto address = validBuilder.street("Baker Street 221B").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid street");
        }

        @Test
        void shouldFailWhenStreetIsBlank() {
            AddressDto address = validBuilder.street("").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Street is required")));
        }

        @Test
        void shouldFailWhenStreetTooLong() {
            AddressDto address = validBuilder.street("a".repeat(31)).build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Street cannot exceed")));
        }
    }

    @Nested
    @DisplayName("City Validation")
    class CityValidation {

        @Test
        void shouldPassWhenCityIsValid() {
            AddressDto address = validBuilder.city("New York").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid city");
        }

        @Test
        void shouldFailWhenCityIsBlank() {
            AddressDto address = validBuilder.city("").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("City is required")));
        }

        @Test
        void shouldFailWhenCityTooLong() {
            AddressDto address = validBuilder.city("a".repeat(31)).build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("City cannot exceed")));
        }
    }

    @Nested
    @DisplayName("State Validation")
    class StateValidation {
        @Test
        void shouldPassWhenStateHasMinLength() {
            AddressDto address = validBuilder.state("NY").build(); // Assuming min is 2
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for 2-char state");
        }

        @Test
        void shouldPassWhenStateHasMaxLength() {
            AddressDto address = validBuilder.state("A".repeat(20)).build(); // Assuming max is 20
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for 20-char state");
        }

        @Test
        void shouldFailWhenStateIsBlank() {
            AddressDto address = validBuilder.state("").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("State is required")));
        }

        @Test
        void shouldFailWhenStateTooShort() {
            AddressDto address = validBuilder.state("A").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("State must be between")));
        }

        @Test
        void shouldFailWhenStateTooLong() {
            AddressDto address = validBuilder.state("A".repeat(21)).build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("State must be between")));
        }
    }

    @Nested
    @DisplayName("Zip Code Validation")
    class ZipCodeValidation {
        @Test
        void shouldPassWhenZipCodeIsValid() {
            AddressDto address = validBuilder.zipCode("94105").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid zip code");
        }

        @Test
        void shouldPassWhenZipCodeHasMinLength() {
            AddressDto address = validBuilder.zipCode("94").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid zip code");
        }

        @Test
        void shouldPassWhenZipCodeHasMaxLength() {
            AddressDto address = validBuilder.zipCode("12345678").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertTrue(violations.isEmpty(), "Expected no violations for valid zip code");
        }

        @Test
        void shouldFailWhenZipCodeIsBlank() {
            AddressDto address = validBuilder.zipCode("").build();
            Set<ConstraintViolation<AddressDto>> violations = validator.validate(address);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Zip code is required")));
        }
    }

}
