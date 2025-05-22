package com.home.user.service.service;


import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;

import java.util.UUID;

public interface UserService {

    void requestAccountCreation(KeycloakUserDTO keycloakUserDTO);

    void requestAccountUpdate(UUID userId, UserDTO userDTO);

    void requestAccountDeletion(UUID userId);

    void addProductForSell(String userId, String productId);

    UserInfo getUserInformation(String userId);
}
