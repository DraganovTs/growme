package com.home.user.service.service.impl;


import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.service.EmailService;
import com.home.user.service.service.UserQueryService;
import com.home.user.service.service.UserService;
import com.home.user.service.service.UserUpdateService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserUpdateService userUpdateService;
    private final UserQueryService userQueryService;
    private final EmailService emailService;

    public UserServiceImpl(UserUpdateService userUpdateService, UserQueryService userQueryService,
                           EmailService emailService) {
        this.userUpdateService = userUpdateService;
        this.userQueryService = userQueryService;
        this.emailService = emailService;
    }


    @Override
    public void requestAccountCreation(KeycloakUserDTO keycloakUserDTO) {
        userUpdateService.syncUserFromKeycloak(keycloakUserDTO);
        emailService.sendAccountCompletionConfirmation(
                userQueryService.getUserById(UUID.fromString(keycloakUserDTO.getUserId()))
                        .getEmail()
        );
    }

    @Override
    public void requestAccountUpdate(UUID userId, UserDTO userDTO) {
        userUpdateService.updateUser(userId, userDTO);
        emailService.sendAccountUpdateConfirmation(
                userQueryService.getUserById(userId)
                        .getEmail()
        );
    }

    @Override
    public void requestAccountDeletion(String userId) {
        userUpdateService.deleteUser(userId);
        emailService.sendAccountDeletionConfirmation(
                userQueryService.getUserById(UUID.fromString(userId)).getEmail()
        );
    }

    @Override
    public void addProductForSell(String userId, String productId) {
        userUpdateService.addOwnedProduct(userId, productId);
        emailService.sendProductAddForSellConfirmation(
                userQueryService.getUserById(UUID.fromString(userId)).getEmail()
        );
    }
}
