package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserDTO Validation Tests")
public class UserDTOTests {

    private Validator validator;
    private UserDTO.UserDTOBuilder validBuilder;

    @BeforeEach
    void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        AddressDTO validAddress = AddressDTO.builder()
                .street("123 Main St")
                .city("San Francisco")
                .state("CA")
                .zipCode("94105")
                .build();

        validBuilder = UserDTO.builder()
                .username("validUser")
                .email("valid@example.com")
                .firstName("validFirstName")
                .lastName("validLastName")
                .phone("1".repeat(11))
                .address(validAddress)
                .roles(List.of("BUYER","SELLER"))
                .accountStatus(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should validate a correct User")
    void shouldValidateCorrectUser(){
        UserDTO user = validBuilder.build();
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Nested
    @DisplayName("Username Validation")
    class UsernameValidation {

        @Test
        void shouldPassWhenUsernameIsValid() {
            UserDTO user = validBuilder.username("valid_user123").build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no validation errors for valid username");
        }

        @Test
        void shouldPassWithUsernameAtMinLength() {
            UserDTO user = validBuilder.username("abc").build(); // 3 characters
            assertTrue(validator.validate(user).isEmpty());
        }

        @Test
        void shouldPassWithUsernameAtMaxLength() {
            UserDTO user = validBuilder.username("a".repeat(20)).build(); // 20 characters
            assertTrue(validator.validate(user).isEmpty());
        }

        @Test
        void shouldFailWhenUsernameIsBlank() {
            UserDTO user = validBuilder.username("").build();
            assertHasViolation(user, "Username is required");
        }



        @Test
        void shouldFailWhenUsernameTooShort() {
            UserDTO user = validBuilder.username("ab").build();
            assertHasViolation(user, "Username must be between 3-20 characters");
        }

        @Test
        void shouldFailWhenUsernameTooLong() {
            UserDTO user = validBuilder.username("a".repeat(21)).build();
            assertHasViolation(user, "Username must be between 3-20 characters");
        }

        @Test
        void shouldFailWhenUsernameHasInvalidCharacters() {
            UserDTO user = validBuilder.username("invalid!user").build();
            assertHasViolation(user, "Username can only contain letters, numbers and underscores");
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation {
        @Test
        void shouldPassWhenEmailIsValid() {
            UserDTO user = validBuilder.email("user@example.com").build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no validation errors for valid email");
        }

        @Test
        void shouldFailWhenEmailIsBlank() {
            UserDTO user = validBuilder.email("").build();
            assertHasViolation(user, "Email is required");
        }

        @Test
        void shouldFailWhenEmailIsInvalid() {
            UserDTO user = validBuilder.email("invalid-email").build();
            assertHasViolation(user, "Email should be valid");
        }
    }

    @Nested
    @DisplayName("First Name Validation")
    class FirstNameValidation {
        @Test
        void shouldPassWhenFirstNameIsValid() {
            UserDTO user = validBuilder.firstName("Alice").build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no violations for valid first name");
        }

        @Test
        void shouldPassWithFirstNameAtMaxLength() {
            UserDTO user = validBuilder.firstName("A".repeat(30)).build();
            assertTrue(validator.validate(user).isEmpty());
        }

        @Test
        void shouldFailWhenFirstNameIsBlank() {
            UserDTO user = validBuilder.firstName("").build();
            assertHasViolation(user, "First name is required");
        }

        @Test
        void shouldFailWhenFirstNameTooLong() {
            UserDTO user = validBuilder.firstName("a".repeat(31)).build();
            assertHasViolation(user, "First name cannot exceed 30 characters");
        }
    }

    @Nested
    @DisplayName("Last Name Validation")
    class LastNameValidation {
        @Test
        void shouldPassWhenLastNameIsValid() {
            UserDTO user = validBuilder.lastName("Smith").build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no violations for valid last name");
        }

        @Test
        void shouldPassWithLastNameAtMaxLength() {
            UserDTO user = validBuilder.lastName("Z".repeat(30)).build();
            assertTrue(validator.validate(user).isEmpty());
        }

        @Test
        void shouldFailWhenLastNameIsBlank() {
            UserDTO user = validBuilder.lastName("").build();
            assertHasViolation(user, "Last name is required");
        }

        @Test
        void shouldFailWhenLastNameTooLong() {
            UserDTO user = validBuilder.lastName("a".repeat(31)).build();
            assertHasViolation(user, "Last name cannot exceed 30 characters");
        }
    }

    @Nested
    @DisplayName("Phone Validation")
    class PhoneValidation {


        @Test
        void shouldPassWithValidPhone() {
            UserDTO user = validBuilder.phone("+12345678901").build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no violations for valid phone");
        }

        @Test
        void shouldPassWithPhoneAtMinDigits() {
            UserDTO user = validBuilder.phone("1234567890").build(); // 10 digits
            assertTrue(validator.validate(user).isEmpty());
        }

        @Test
        void shouldPassWithPhoneAtMaxDigits() {
            UserDTO user = validBuilder.phone("+123456789012345").build(); // 15 digits with +
            assertTrue(validator.validate(user).isEmpty());
        }

        @Test
        void shouldFailWhenPhoneIsInvalid() {
            UserDTO user = validBuilder.phone("12345").build();
            assertHasViolation(user, "Phone number must be 10-15 digits, optionally starting with +");
        }
    }

    @Nested
    @DisplayName("Address Validation")
    class AddressValidation {
        @Test
        void shouldPassWhenAddressIsValid() {
            UserDTO user = validBuilder.address(AddressDTO.builder()
                    .street("221B Baker Street")
                    .city("London")
                    .state("UK")
                    .zipCode("NW16XE")
                    .build()).build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no violations for valid address");
        }

        @Test
        void shouldFailWhenAddressIsNull() {
            UserDTO user = validBuilder.address(null).build();
            assertHasViolation(user, "Address is required");
        }

        @Test
        void shouldFailWhenStreetIsNull() {
            AddressDTO address = AddressDTO.builder()
                    .street(null)
                    .city("City")
                    .state("CA")
                    .zipCode("12345")
                    .build();

            UserDTO user = validBuilder.address(address).build();
            assertFalse(validator.validate(user).isEmpty());
        }
    }

    @Nested
    @DisplayName("Roles Validation")
    class RolesValidation {
        @Test
        void shouldPassWhenRolesAreValid() {
            UserDTO user = validBuilder.roles(List.of("Buyer", "Seller")).build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no violations for valid roles");
        }

        @Test
        void shouldPassWithOneValidRole() {
            UserDTO user = validBuilder.roles(List.of("Buyer")).build();
            assertTrue(validator.validate(user).isEmpty());
        }

        @Test
        void shouldFailWhenRolesEmpty() {
            UserDTO user = validBuilder.roles(List.of()).build();
            assertHasViolation(user, "At least one role is required");
        }

        @Test
        void shouldFailWhenRoleIsBlank() {
            UserDTO user = validBuilder.roles(List.of(" ")).build();
            assertHasViolation(user, "must not be blank");
        }
    }

    @Nested
    @DisplayName("Account Status Validation")
    class AccountStatusValidation {
        @Test
        void shouldPassWithValidAccountStatus() {
            UserDTO user = validBuilder.accountStatus(AccountStatus.ACTIVE).build();
            Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no violations for valid account status");
        }


    }

    private void assertHasViolation(UserDTO user, String expectedMessage) {
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Expected validation error");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains(expectedMessage)),
                "Expected message containing: " + expectedMessage);
    }
}
