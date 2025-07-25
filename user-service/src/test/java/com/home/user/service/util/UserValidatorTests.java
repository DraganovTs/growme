package com.home.user.service.util;

import com.home.user.service.exception.EmailAlreadyExistsException;
import com.home.user.service.exception.UsernameAlreadyExistsException;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("UserValidator Tests")
public class UserValidatorTests {
    private UserValidator validator;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        validator = new UserValidator(userRepository);
    }

    @Nested
    @DisplayName("validateKeycloakUser")
    class ValidateKeycloakUser {

        @Test
        void shouldPassWithValidInput() {
            KeycloakUserDTO dto = KeycloakUserDTO.builder()
                    .userId(UUID.randomUUID().toString())
                    .email("user@example.com")
                    .build();
            assertDoesNotThrow(() -> validator.validateKeycloakUser(dto));
        }

        @Test
        void shouldThrowWhenDtoIsNull() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateKeycloakUser(null));
        }

        @Test
        void shouldThrowForInvalidEmail() {
            KeycloakUserDTO dto = KeycloakUserDTO.builder()
                    .userId(UUID.randomUUID().toString())
                    .email("invalid-email")
                    .build();
            assertThrows(IllegalArgumentException.class, () -> validator.validateKeycloakUser(dto));
        }

        @Test
        void shouldThrowForInvalidUUID() {
            KeycloakUserDTO dto =  KeycloakUserDTO.builder()
                    .userId("bad-uuid")
                    .email("user@example.com")
                    .build();
            assertThrows(IllegalArgumentException.class, () -> validator.validateKeycloakUser(dto));
        }
    }

    @Test
    @DisplayName("validateUserDTO should throw if null")
    void validateUserDTO_null() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateUserDTO(null));
    }

    @Nested
    @DisplayName("validateUserUpdate")
    class ValidateUserUpdate {

        @Test
        void shouldThrowForChangedEmailThatExists() {
            User user = new User();
            user.setEmail("old@example.com");
            user.setUsername("olduser");

            UserDTO dto = UserDTO.builder()
                    .email("new@example.com")
                    .username("olduser")
                    .build();


            when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> validator.validateUserUpdate(user, dto));
        }

        @Test
        void shouldThrowForChangedUsernameThatExists() {
            User user = new User();
            user.setEmail("same@example.com");
            user.setUsername("olduser");

            UserDTO dto = UserDTO.builder()
                    .username("newuser")
                    .email("same@example.com")
                    .build();


            when(userRepository.existsByUsername("newuser")).thenReturn(true);

            assertThrows(UsernameAlreadyExistsException.class, () -> validator.validateUserUpdate(user, dto));
        }
    }

    @Nested
    @DisplayName("checkForExistingCredentials")
    class ExistingCredentials {

        @Test
        void shouldThrowForExistingEmail() {
            when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);
            assertThrows(EmailAlreadyExistsException.class,
                    () -> validator.checkForExistingCredentials("test@mail.com", "validUsername"));
        }

        @Test
        void shouldThrowForExistingUsername() {
            when(userRepository.existsByUsername("takenUser")).thenReturn(true);
            assertThrows(UsernameAlreadyExistsException.class,
                    () -> validator.checkForExistingCredentials("test@mail.com", "takenUser"));
        }
    }

    @Nested
    @DisplayName("validateIds")
    class ValidateIds {

        @Test
        void shouldPassWithValidUUIDs() {
            assertDoesNotThrow(() ->
                    validator.validateIds(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        }

        @Test
        void shouldThrowForNullProductId() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.validateIds(UUID.randomUUID().toString(), null));
        }

        @Test
        void shouldThrowForInvalidProductIdFormat() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.validateIds(UUID.randomUUID().toString(), "bad-id"));
        }

        @Test
        void shouldThrowForInvalidUserIdFormat() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.validateIds("invalid-uuid", UUID.randomUUID().toString()));
        }
    }

    @Nested
    @DisplayName("validateRole")
    class ValidateRole {

        @Test
        void shouldPassForValidRoles() {
            assertDoesNotThrow(() -> validator.validateRole("admin"));
            assertDoesNotThrow(() -> validator.validateRole("SELLER"));
        }

        @Test
        void shouldThrowForEmptyRole() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateRole(""));
        }

        @Test
        void shouldThrowForInvalidRole() {
            assertThrows(IllegalArgumentException.class, () -> validator.validateRole("GUEST"));
        }
    }

    @Nested
    @DisplayName("Private validations")
    class PrivateValidations {

        @Test
        void shouldThrowForInvalidEmailFormat() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.checkForExistingCredentials("bademail", "validUser"));
        }

        @Test
        void shouldThrowForShortUsername() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.checkForExistingCredentials("test@mail.com", "ab"));
        }

        @Test
        void shouldThrowForLongUsername() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.checkForExistingCredentials("test@mail.com", "a".repeat(21)));
        }

        @Test
        void shouldPassForValidEmailAndUsername() {
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);

            assertDoesNotThrow(() ->
                    validator.checkForExistingCredentials("user@mail.com", "validUser"));
        }
    }
}
