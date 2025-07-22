package com.home.user.service.service.impl;

import com.home.user.service.exception.*;
import com.home.user.service.mapper.UserMapper;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.EventPublisherService;
import com.home.user.service.util.UserValidator;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Command Service Tests")
@ExtendWith(MockitoExtension.class)
public class UserCommandServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator validator;

    @Mock
    private EventPublisherService eventPublisherService;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    private KeycloakUserDTO testKeycloakUser;
    private UserDTO testUserDTO;
    private User testUser;
    private UUID userId;
    private String productId;
    private String orderId;

    @BeforeEach
    void setup() {
        userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        productId = "660e8400-e29b-41d4-a716-446655440111";
        orderId = "770e8400-e29b-41d4-a716-446655440222";

        testKeycloakUser = KeycloakUserDTO.builder()
                .userId(userId.toString())
                .email("john.doe@example.com")
                .username("john_doe")
                .build();

        testUserDTO = UserDTO.builder()
                .username("updated_username")
                .email("updated.email@example.com")
                .firstName("Updated")
                .lastName("User")
                .roles(List.of("Seller"))
                .build();

        testUser = new User();
        testUser.setUserId(userId);
        testUser.setUsername("john_doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setOwnedProductIds(new ArrayList<>());
        testUser.setPurchasedOrderIds(new ArrayList<>());
    }

    @Nested
    @DisplayName("Sync User from Keycloak")
    class SyncUserTests {

        @Test
        @DisplayName("Should sync user successfully")
        void shouldSyncUserSuccessfully() {
            when(userRepository.existsById(userId)).thenReturn(false);
            when(userMapper.mapKeyCloakDtoToUser(testKeycloakUser)).thenReturn(testUser);
            when(userRepository.save(testUser)).thenReturn(testUser);

            assertDoesNotThrow(() -> userCommandService.syncUserFromKeycloak(testKeycloakUser));
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistException when user exists")
        void shouldThrowUserAlreadyExistExceptionWhenUserExists() {
            when(userRepository.existsById(userId)).thenReturn(true);

            assertThrows(UserAlreadyExistException.class,
                    () -> userCommandService.syncUserFromKeycloak(testKeycloakUser));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw EmailAlreadyExistsException on email constraint violation")
        void shouldThrowEmailAlreadyExistsExceptionOnEmailConstraintViolation() {
            when(userRepository.existsById(userId)).thenReturn(false);

            ConstraintViolationException cve = mock(ConstraintViolationException.class);
            when(cve.getConstraintName()).thenReturn("email");
            DataIntegrityViolationException divException = new DataIntegrityViolationException("Conflict", cve);

            doThrow(divException).when(userRepository).save(any());

            assertThrows(EmailAlreadyExistsException.class,
                    () -> userCommandService.syncUserFromKeycloak(testKeycloakUser));
        }

        @Test
        @DisplayName("Should throw UsernameAlreadyExistsException on username constraint violation")
        void shouldThrowUsernameAlreadyExistsExceptionOnUsernameConstraintViolation() {
            when(userRepository.existsById(userId)).thenReturn(false);

            ConstraintViolationException cve = mock(ConstraintViolationException.class);
            when(cve.getConstraintName()).thenReturn("username");
            DataIntegrityViolationException divException = new DataIntegrityViolationException("Conflict", cve);

            doThrow(divException).when(userRepository).save(any());

            assertThrows(UsernameAlreadyExistsException.class,
                    () -> userCommandService.syncUserFromKeycloak(testKeycloakUser));
        }
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            doNothing().when(validator).validateUserUpdate(testUser, testUserDTO);
            when(userMapper.updateUserInitialAccount(testUser, testUserDTO)).thenReturn(testUser);
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(userMapper.mapUserToUserDTO(testUser)).thenReturn(testUserDTO);

            UserDTO result = userCommandService.updateUser(userId, testUserDTO);

            verify(userRepository).findById(userId);
            verify(validator).validateUserUpdate(testUser, testUserDTO);
            verify(userRepository).save(testUser);
            verify(eventPublisherService).publishUserCreated(any());
            assertEquals(testUserDTO, result);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user doesn't exist")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userCommandService.updateUser(userId, testUserDTO));

            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DataConflictException on validation failure")
        void shouldThrowExceptionOnValidationFailureDuringUpdate() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            doThrow(new DataConflictException("DATA_CONFLICT", "Invalid data"))
                    .when(validator).validateUserUpdate(testUser, testUserDTO);

            assertThrows(DataConflictException.class,
                    () -> userCommandService.updateUser(userId, testUserDTO));

            verify(userRepository).findById(userId);
            verify(validator).validateUserUpdate(testUser, testUserDTO);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Owned Products Operations")
    class OwnedProductsTests {

        @Test
        @DisplayName("Should add owned product successfully")
        void shouldAddOwnedProductSuccessfully() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            doNothing().when(validator).validateIds(userId.toString(), productId);

            assertDoesNotThrow(() -> userCommandService.addOwnedProduct(userId.toString(), productId));

            verify(validator).validateIds(userId.toString(), productId);
            verify(userRepository).findById(userId);
            verify(userRepository).save(testUser);
            assertTrue(testUser.getOwnedProductIds().contains(UUID.fromString(productId)));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when adding product for nonexistent user")
        void shouldThrowUserNotFoundExceptionWhenAddingProductForNonexistentUser() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            doNothing().when(validator).validateIds(userId.toString(), productId);

            assertThrows(UserNotFoundException.class,
                    () -> userCommandService.addOwnedProduct(userId.toString(), productId));

            verify(validator).validateIds(userId.toString(), productId);
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should do nothing if product already exists")
        void shouldDoNothingIfProductAlreadyExists() {
            testUser.getOwnedProductIds().add(UUID.fromString(productId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            doNothing().when(validator).validateIds(userId.toString(), productId);

            assertDoesNotThrow(() -> userCommandService.addOwnedProduct(userId.toString(), productId));

            verify(validator).validateIds(userId.toString(), productId);
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should delete owned product successfully")
        void shouldDeleteOwnedProductSuccessfully() {
            testUser.getOwnedProductIds().add(UUID.fromString(productId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            doNothing().when(validator).validateIds(userId.toString(), productId);

            assertDoesNotThrow(() -> userCommandService.deleteOwnedProduct(userId.toString(), productId));

            verify(validator).validateIds(userId.toString(), productId);
            verify(userRepository).findById(userId);
            assertFalse(testUser.getOwnedProductIds().contains(UUID.fromString(productId)));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when deleting product for nonexistent user")
        void shouldThrowUserNotFoundExceptionWhenDeletingProductForNonexistentUser() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            doNothing().when(validator).validateIds(userId.toString(), productId);

            assertThrows(UserNotFoundException.class,
                    () -> userCommandService.deleteOwnedProduct(userId.toString(), productId));

            verify(validator).validateIds(userId.toString(), productId);
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("Should do nothing if product doesn't exist in owned products")
        void shouldDoNothingIfProductDoesNotExistInOwnedProducts() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            doNothing().when(validator).validateIds(userId.toString(), productId);

            assertDoesNotThrow(() -> userCommandService.deleteOwnedProduct(userId.toString(), productId));

            verify(validator).validateIds(userId.toString(), productId);
            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete User")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            when(userRepository.existsById(userId)).thenReturn(true);
            doNothing().when(userRepository).deleteById(userId);

            assertDoesNotThrow(() -> userCommandService.deleteUser(userId));

            verify(userRepository).existsById(userId);
            verify(userRepository).deleteById(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when deleting nonexistent user")
        void shouldThrowUserNotFoundExceptionWhenDeletingNonexistentUser() {
            when(userRepository.existsById(userId)).thenReturn(false);

            assertThrows(UserNotFoundException.class,
                    () -> userCommandService.deleteUser(userId));

            verify(userRepository).existsById(userId);
            verify(userRepository, never()).deleteById(userId);
        }
    }

    @Nested
    @DisplayName("Owner Order Operations")
    class OwnerOrderTests {

        @Test
        @DisplayName("Should add owner order successfully")
        void shouldAddOwnerOrderSuccessfully() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            assertDoesNotThrow(() -> userCommandService.addOwnerOrder(userId.toString(), orderId));

            verify(userRepository).findById(userId);
            verify(userRepository).save(testUser);
            assertTrue(testUser.getPurchasedOrderIds().contains(UUID.fromString(orderId)));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when adding order for nonexistent user")
        void shouldThrowUserNotFoundExceptionWhenAddingOrderForNonexistentUser() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userCommandService.addOwnerOrder(userId.toString(), orderId));

            verify(userRepository).findById(userId);
            verify(userRepository, never()).save(any());
        }
    }
}