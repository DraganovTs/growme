package com.home.user.service.service;

import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;

import java.util.UUID;

public interface UserUpdateService {
    void syncUserFromKeycloak(KeycloakUserDTO keycloakUserDTO);
    UserDTO updateUser(UUID id,UserDTO userDTO);
    void addOwnedProduct(String userId, String productId);
    void markProfileAsCompleted(String userId);
    void deleteUser(UUID userId);

    void deleteOwnedProduct(String userId, String productId);
}
