package com.home.user.service.util;

import com.home.user.service.exception.EmailAlreadyExistsException;
import com.home.user.service.exception.UsernameAlreadyExistsException;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.service.UserQueryService;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class UserValidator {

    private final UserQueryService userQueryService;
    private static final Set<String> VALID_ROLES =
            Set.of("ADMIN", "SELLER", "BUYER");

    public UserValidator(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }


    public void validateKeycloakUser(KeycloakUserDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("KeycloakUserDTO cannot be null");
        }
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        try {
            UUID.fromString(dto.getUserId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }
        if (dto.getEmail() == null || !dto.getEmail().matches(".+@.+\\..+")) {
            throw new IllegalArgumentException("Valid email is required");
        }
    }

    public void validateUserDTO(UserDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }
    }

    public void validateUserUpdate(User existingUser, UserDTO updateDTO) {
        if (updateDTO == null) {
            throw new IllegalArgumentException("Update DTO cannot be null");
        }
        if (!updateDTO.getEmail().equals(existingUser.getEmail())) {
            validateEmail(updateDTO.getEmail());
        }
        if (!updateDTO.getUsername().equals(existingUser.getUsername())) {
            validateUsername(updateDTO.getUsername());
        }
    }

    public void checkForExistingCredentials(String email, String username) {
        validateEmail(email);
        validateUsername(username);
    }


    public void validateIds(String userId, String productId) {
        validateUserId(userId);
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        try {
            UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid product ID format");
        }
    }

    public void validateUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        try {
            UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }
    }

    public void validateRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }
        if (!VALID_ROLES.contains(role.toUpperCase())) {
            throw new IllegalArgumentException(
                    String.format("Invalid role '%s'. Allowed values: %s",
                            role, String.join(", ", VALID_ROLES))
            );
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches(".+@.+\\..+")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (userQueryService.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("Username must be 4-20 characters");
        }
        if (userQueryService.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
    }

}