package com.home.user.service.service.impl;


import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.exception.*;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.service.EmailService;
import com.home.user.service.service.UserQueryService;
import com.home.user.service.service.UserService;
import com.home.user.service.service.UserCommandService;
import com.home.user.service.util.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final EmailService emailService;
    private final UserValidator validator;

    public UserServiceImpl(UserCommandService userCommandService, UserQueryService userQueryService,
                           EmailService emailService, UserValidator validator) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.emailService = emailService;
        this.validator = validator;
    }

    @Override
    @Transactional
    public void requestAccountCreation(KeycloakUserDTO keycloakUserDTO) {
        validator.validateKeycloakUser(keycloakUserDTO);
        log.info("Initiating account creation for user: {}", keycloakUserDTO.getUserId());

        try {
            userCommandService.syncUserFromKeycloak(keycloakUserDTO);
            emailService.sendAccountCompletionConfirmation(keycloakUserDTO.getEmail());
            log.info("Account created successfully for user: {}", keycloakUserDTO.getUserId());
        } catch (UserAlreadyExistException e) {
            log.warn("Duplicate user registration - ID: {}", keycloakUserDTO.getUserId(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists", e);
        } catch (EmailAlreadyExistsException e) {
            log.warn("Duplicate email registration - Email: {}", keycloakUserDTO.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered", e);
        }
    }

    @Override
    @Transactional
    public void requestAccountUpdate(UUID userId, UserDTO userDTO) {
        validator.validateUserDTO(userDTO);
        log.info("Processing update for user: {}", userId);

        try {
            UserDTO updatedUser = userCommandService.updateUser(userId, userDTO);
            emailService.sendAccountUpdateConfirmation(updatedUser.getEmail());
            log.info("Account update completed for user: {}", userId);
        } catch (UserNotFoundException e) {
            log.error("Update attempt for non-existent user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        } catch (EmailAlreadyExistsException | UsernameAlreadyExistsException e) {
            log.warn("Credential conflict during update for user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void requestAccountDeletion(UUID userId) {
        log.info("Processing deletion for user: {}", userId);

        try {
            String email = userQueryService.getUserEmail(userId);
            userCommandService.deleteUser(userId);
            emailService.sendAccountDeletionConfirmation(email);
            log.info("Account deletion completed for user: {}", userId);
        } catch (UserNotFoundException e) {
            log.error("Deletion attempt for non-existent user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        }
    }

    @Override
    @Transactional
    public void addProductForSell(String userId, String productId) {
        validator.validateIds(userId, productId);
        log.info("Adding product {} for user {}", productId, userId);

        try {
            String email = userQueryService.getUserEmail(UUID.fromString(userId));
            userCommandService.addOwnedProduct(userId, productId);
            emailService.sendProductAddForSellConfirmation(email);
            log.info("Product {} successfully added for user {}", productId, userId);
        } catch (UserNotFoundException e) {
            log.error("Product addition attempt for non-existent user: {}", userId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid ID format provided", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format", e);
        }
    }

    @Override
    public UserInfo getUserInformation(String userId) {
        validator.validateUserId(userId);
        return userQueryService.getUserInformation(userId);
    }

    @Override
    @Transactional
    public void requestSyncUserData(KeycloakUserDTO request) {
        validator.validateKeycloakUser(request);
        log.info("Syncing user data for: {}", request.getUserId());

        if (!userQueryService.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        userCommandService.syncUserData(request);
    }

    @Override
    public boolean existsById(String userId) {
        return userQueryService.existsById(UUID.fromString(userId));
    }

    @Override
    public Boolean requestCheckUserProfile(UUID userId) {
        return userQueryService.checkUserProfileIsComplete(userId);
    }
}
