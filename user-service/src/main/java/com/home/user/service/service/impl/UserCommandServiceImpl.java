package com.home.user.service.service.impl;

import com.home.growme.common.module.events.UserCreatedEvent;
import com.home.user.service.exception.*;
import com.home.user.service.mapper.UserMapper;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.EventPublisherService;
import com.home.user.service.service.UserCommandService;
import com.home.user.service.util.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator validator;
    private final EventPublisherService eventPublisherService;


    public UserCommandServiceImpl(UserRepository userRepository, UserMapper userMapper,
                                  UserValidator validator, EventPublisherService eventPublisherService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.validator = validator;
        this.eventPublisherService = eventPublisherService;
    }

    @Override
    public void syncUserFromKeycloak(KeycloakUserDTO keycloakUserDTO) {

        UUID userId = UUID.fromString(keycloakUserDTO.getUserId());

        if (userRepository.existsById(userId)) {
            throw new UserAlreadyExistException("User already exists with ID: " + userId);
        }

        validator.checkForExistingCredentials(keycloakUserDTO.getEmail(), keycloakUserDTO.getUsername());

        try {
            User newUser = userMapper.mapKeyCloakDtoToUser(keycloakUserDTO);
            userRepository.save(newUser);
            log.info("Successfully synced user with ID: {}", userId);
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolation(e, keycloakUserDTO);
        }
    }


    @Override
    public UserDTO updateUser(UUID userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for update: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        validator.validateUserUpdate(user, userDTO);
        user = userMapper.updateUserInitialAccount(user, userDTO);

        if (userDTO.getRoles() != null) {
            log.debug("Publishing role assignments for user: {}", userId);
            userDTO.getRoles().forEach(role -> eventPublisherService.publishRoleAssignment(
                    userId.toString(),
                    role));
        }


        User savedUser = userRepository.save(user);
        UserCreatedEvent event = new UserCreatedEvent(userId.toString(), savedUser.getFirstName(), savedUser.getEmail());
        eventPublisherService.publishUserCreated(event);
        log.info("User updated successfully: {}", userId);
        return userMapper.mapUserToUserDTO(savedUser);
    }

    @Override
    public void addOwnedProduct(String userId, String productId) {
        validator.validateIds(userId, productId);
        UUID productUUID = UUID.fromString(productId);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!user.getOwnedProductIds().contains(productUUID)) {
            user.getOwnedProductIds().add(productUUID);
            userRepository.save(user);
            log.info("Added product {} to user {}", productId, userId);
        }
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("Deleted user with ID: {}", userId);
    }

    @Override
    public void deleteOwnedProduct(String userId, String productId) {
        validator.validateIds(userId, productId);
        UUID productUUID = UUID.fromString(productId);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.getOwnedProductIds().remove(productUUID);
        log.info("Delete product {} to user {}", productId, userId);
    }

    @Override
    public void addOwnerOrder(String orderUserId, String orderId) {
        User user = userRepository.findById(UUID.fromString(orderUserId))
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + orderUserId));


        user.getPurchasedOrderIds().add(UUID.fromString(orderId));
        userRepository.save(user);
    }

    @Override
    public void syncUserData(KeycloakUserDTO request) {

        UUID userId = UUID.fromString(request.getUserId());
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException( "User not found whit id: "+ userId);
        }

        User user = userOptional.get();
        userMapper.updateUserFromDto(request, user);
        userRepository.save(user);

        log.info("Updated user data for user: {}", userId);
    }


    private void handleDataIntegrityViolation(DataIntegrityViolationException e, KeycloakUserDTO dto) {
        log.error("Data integrity violation while syncing user", e);

        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
            if (cve.getConstraintName().contains("email")) {
                throw new EmailAlreadyExistsException(dto.getEmail());
            } else if (cve.getConstraintName().contains("username")) {
                throw new UsernameAlreadyExistsException(dto.getUsername());
            }
        }
        throw new DataConflictException("DATA_CONFLICT", "Failed to sync user due to data conflict");
    }

}
