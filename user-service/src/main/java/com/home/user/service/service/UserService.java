package com.home.user.service.service;


import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/**
 * The UserService interface provides a set of methods for managing user-related
 * operations within the system. This service acts as a high-level abstraction
 * that combines functionalities such as user account management, product association,
 * and fetching user information.
 */
public interface UserService {

    void requestAccountCreation(@Valid KeycloakUserDTO keycloakUserDTO);

    void requestAccountUpdate(UUID userId, UserDTO userDTO);

    void requestAccountDeletion(UUID userId);

    void addProductForSell(String userId, String productId);

    UserInfo getUserInformation(String userId);

    void requestSyncUserData(@Valid KeycloakUserDTO request);

    boolean existsById(@NotBlank(message = "User ID is required") String userId);
}
