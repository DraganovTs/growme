package com.home.user.service.service.impl;


import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.mapper.UserMapper;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.model.enums.AccountStatus;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.util.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidator validator;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    private User testUser;
    private UserDTO testUserDTO;
    private UserInfo testUserInfo;
    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        testUser = new User();
        testUser.setUserId(userId);
        testUser.setUsername("johndoe");
        testUser.setEmail("john.doe@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testUserDTO = UserDTO.builder()
                .username("johndoe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        testUserInfo = UserInfo.builder()
                .username("johndoe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Nested
    class GetUsersByRole {

        @Test
        @DisplayName("Should return users by role")
        void shouldReturnUsersByRole() {
            String role = "Admin";
            when(userRepository.findByRolesContaining(role)).thenReturn(List.of(testUser));
            when(userMapper.mapUserToUserDTO(testUser)).thenReturn(testUserDTO);

            List<UserDTO> result = userQueryService.getUsersByRole(role);

            assertEquals(1, result.size());
            assertEquals("johndoe", result.get(0).getUsername());
        }

        @Test
        @DisplayName("Should return empty list if no users found")
        void shouldReturnEmptyList() {
            when(userRepository.findByRolesContaining("EmptyRole")).thenReturn(Collections.emptyList());

            List<UserDTO> result = userQueryService.getUsersByRole("EmptyRole");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate role")
        void shouldValidateRole() {
            String role = "Admin";

            doNothing().when(validator).validateRole(role);
            when(userRepository.findByRolesContaining(role)).thenReturn(List.of());

            userQueryService.getUsersByRole(role);

            verify(validator).validateRole(role);
        }
    }

    @Nested
    class GetUserByIdTests {

        @Test
        void shouldReturnUserDTO() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userMapper.mapUserToUserDTO(testUser)).thenReturn(testUserDTO);

            UserDTO result = userQueryService.getUserById(userId);

            assertEquals("johndoe", result.getUsername());
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userQueryService.getUserById(userId));
        }
    }

    @Nested
    class GetUserEntityTests {

        @Test
        void shouldReturnEntity() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            User result = userQueryService.getUserEntityById(userId);

            assertEquals(userId, result.getUserId());
        }

        @Test
        void shouldThrowIfEntityNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userQueryService.getUserEntityById(userId));
        }
    }

    @Nested
    class EmailLookupTests {

        @Test
        void shouldReturnEmail() {
            when(userRepository.findEmailById(userId)).thenReturn(Optional.of("john.doe@example.com"));

            String result = userQueryService.getUserEmail(userId);

            assertEquals("john.doe@example.com", result);
        }

        @Test
        void shouldThrowIfEmailNotFound() {
            when(userRepository.findEmailById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userQueryService.getUserEmail(userId));
        }
    }

    @Nested
    class OwnedProductsTests {

        @Test
        void shouldReturnProductIds() {
            List<UUID> productIds = List.of(UUID.randomUUID(), UUID.randomUUID());
            testUser.setOwnedProductIds(productIds);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            List<UUID> result = userQueryService.getUsersOwnedProducts(userId.toString());

            assertEquals(productIds, result);
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userQueryService.getUsersOwnedProducts(userId.toString()));
        }

        @Test
        void shouldValidateUserId() {
            doNothing().when(validator).validateUserId(userId.toString());
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            userQueryService.getUsersOwnedProducts(userId.toString());

            verify(validator).validateUserId(userId.toString());
        }
    }

    @Nested
    class UserInfoTests {

        @Test
        void shouldReturnUserInfo() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
            when(userMapper.mapUserToUserInfo(testUser)).thenReturn(testUserInfo);

            UserInfo result = userQueryService.getUserInformation(userId.toString());

            assertEquals("johndoe", result.getUsername());
        }

        @Test
        void shouldThrowIfUserNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userQueryService.getUserInformation(userId.toString()));
        }
    }

    @Nested
    class ExistsChecks {

        @Test
        void shouldReturnTrueIfEmailExists() {
            when(userRepository.existsByEmail("email")).thenReturn(true);

            assertTrue(userQueryService.existsByEmail("email"));
        }

        @Test
        void shouldReturnFalseIfEmailNotExists() {
            when(userRepository.existsByEmail("email")).thenReturn(false);

            assertFalse(userQueryService.existsByEmail("email"));
        }

        @Test
        void shouldReturnTrueIfUsernameExists() {
            when(userRepository.existsByUsername("user")).thenReturn(true);

            assertTrue(userQueryService.existsByUsername("user"));
        }

        @Test
        void shouldReturnFalseIfUsernameNotExists() {
            when(userRepository.existsByUsername("user")).thenReturn(false);

            assertFalse(userQueryService.existsByUsername("user"));
        }

        @Test
        void shouldReturnTrueIfIdExists() {
            when(userRepository.existsById(userId)).thenReturn(true);

            assertTrue(userQueryService.existsById(userId));
        }

        @Test
        void shouldReturnFalseIfIdNotExists() {
            when(userRepository.existsById(userId)).thenReturn(false);

            assertFalse(userQueryService.existsById(userId));
        }
    }

    @Nested
    class ProfileCompleteCheck {

        @Test
        void shouldReturnTrueIfActive() {
            testUser.setAccountStatus(AccountStatus.ACTIVE);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            assertTrue(userQueryService.checkUserProfileIsComplete(userId));
        }

        @Test
        void shouldReturnFalseIfInactive() {
            testUser.setAccountStatus(AccountStatus.INACTIVE);

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            assertFalse(userQueryService.checkUserProfileIsComplete(userId));
        }

        @Test
        void shouldThrowIfNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userQueryService.checkUserProfileIsComplete(userId));
        }
    }

}
