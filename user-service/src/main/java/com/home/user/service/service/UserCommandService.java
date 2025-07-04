package com.home.user.service.service;

import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import jakarta.validation.Valid;

import java.util.UUID;

/**
 * The UserUpdateService interface defines methods for handling user updates
 * and managing various user-related operations within the system. It facilitates
 * synchronization of user data, updates to user profiles, and management of user
 * associations, including owned products and order ownership.
 */
public interface UserCommandService {
    void syncUserFromKeycloak(@Valid KeycloakUserDTO keycloakUserDTO);
    UserDTO updateUser(UUID id,@Valid UserDTO userDTO);
    void addOwnedProduct(String userId, String productId);
    void deleteUser(UUID userId);
    void deleteOwnedProduct(String userId, String productId);
    void addOwnerOrder(String orderUserId, String orderId);

    void syncUserData(@Valid KeycloakUserDTO request);
}
