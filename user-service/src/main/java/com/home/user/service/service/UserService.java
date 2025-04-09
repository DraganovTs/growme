package com.home.user.service.service;

import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    KeycloakUserDTO syncUserFromKeycloak(KeycloakUserDTO keycloakUserDTO);
    UserDTO updateUser(UUID id, UserDTO userDTO);
    UserDTO getUserById(UUID id);
    void markProfileAsComplete(UUID id);
    void deleteUser(UUID id);
    List<UserDTO> getUsersByRole(String role);
    void addProductToUser(String userId, String productId);
}
