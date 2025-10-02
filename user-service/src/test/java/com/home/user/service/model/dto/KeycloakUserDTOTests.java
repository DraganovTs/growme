package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
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

@DisplayName("KeycloakUserDTO Validation Tests")
public class KeycloakUserDTOTests {

    private Validator validator;
    private KeycloakUserDTO.KeycloakUserDTOBuilder validBuilder;

    @BeforeEach
    void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validBuilder = KeycloakUserDTO.builder()
                .userId("550e8400-e29b-41d4-a716-446655440000")
                .username("john_doe")
                .email("john.doe@example.com")
                .accountStatus(AccountStatus.PENDING);
    }

    @Test
    @DisplayName("Should validate a correct KeycvloakUser")
    void shouldValidateCorrectKeycloakUser(){
        KeycloakUserDTO keycloakUser = validBuilder.build();
        Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Nested
    @DisplayName("UserId Validation")
    class UserIdValidation{

        @Test
        @DisplayName("Should pass when userId is valid UUID")
        void shouldPassWhenUserIdIsValid() {
            KeycloakUserDTO keycloakUser = validBuilder.userId("550e8400-e29b-41d4-a716-446655440000").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail when userId is too empty")
        void shouldFailWhenUserIdIsBlank(){
            KeycloakUserDTO keycloakUser = validBuilder.userId("").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("User ID is required")));
        }

        @Test
        @DisplayName("Should fail when userId is too short")
        void shouldFailWhenUserIdIsTooShort(){
            KeycloakUserDTO keycloakUser = validBuilder.userId("550e8400-e29b-41d4-a716-446655440").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Id in UUID format")));
        }

        @Test
        @DisplayName("Should fail when userId is too long")
        void shouldFailWhenUserIdIsTooLong(){
            KeycloakUserDTO keycloakUser = validBuilder.userId("550e8400-e29b-41d4-a716-446655440222222444422").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Id in UUID format")));
        }
    }


    @Nested
    @DisplayName("Username Validation")
    class UserNameValidation{

        @Test
        @DisplayName("Should pass when username is valid")
        void shouldPassWhenUsernameIsValid() {
            KeycloakUserDTO keycloakUser = validBuilder.username("valid_user123").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertTrue(violations.isEmpty());
        }


        @Test
        @DisplayName("Should fail when username is empty")
        void shouldFailWhenUsernameIsEmpty(){
            KeycloakUserDTO keycloakUser = validBuilder.username("").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v-> v.getMessage().contains("Username is required")));
        }


        @Test
        @DisplayName("Should fail when username is too short")
        void shouldFailWhenUsernameIsTooShort(){
            KeycloakUserDTO keycloakUser = validBuilder.username("22").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v-> v.getMessage().contains("Username must be between 3-20 characters")));
        }


        @Test
        @DisplayName("Should fail when username is too long")
        void shouldFailWhenUsernameIsTooLong(){
            KeycloakUserDTO keycloakUser = validBuilder.username("12345678912345678912345").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v-> v.getMessage().contains("Username must be between 3-20 characters")));
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation{

        @Test
        @DisplayName("Should pass when email is valid")
        void shouldPassWhenEmailIsValid() {
            KeycloakUserDTO keycloakUser = validBuilder.email("valid.email@example.com").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail if email is empty")
        void shouldFailIfEmailIsEmpty(){
            KeycloakUserDTO keycloakUser = validBuilder.email("").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v-> v.getMessage().contains("Email is required")));

        }

        @Test
        @DisplayName("Should fail if email is invalid")
        void shouldFailIfEmailIsInvalid(){
            KeycloakUserDTO keycloakUser = validBuilder.email("invalid-email").build();
            Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v-> v.getMessage().contains("Invalid email format")));

        }
    }


    @Nested
    @DisplayName("Account Status Validation")
    class AccountStatusValidation{


        @Test
        @DisplayName("Should pass if accountStatus is valid")
        void shouldPassIfAccountStatusIsValid() {
            for (AccountStatus status : AccountStatus.values()) {
                KeycloakUserDTO keycloakUser = validBuilder.accountStatus(status).build();
                Set<ConstraintViolation<KeycloakUserDTO>> violations = validator.validate(keycloakUser);
                assertTrue(violations.isEmpty(), "Failed for status: " + status);
            }
        }
    }

}
