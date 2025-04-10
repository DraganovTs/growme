package com.home.user.service.service.impl;

import com.home.user.service.exception.UserAlreadyExistException;
import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.kafka.publisher.RoleEventPublisher;
import com.home.user.service.mapper.UserMapper;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.UserUpdateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserUpdateServiceImpl implements UserUpdateService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleEventPublisher roleEventPublisher;

    public UserUpdateServiceImpl(UserRepository userRepository, UserMapper userMapper,
                                 RoleEventPublisher roleEventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleEventPublisher = roleEventPublisher;
    }

    @Override
    public void syncUserFromKeycloak(KeycloakUserDTO keycloakUserDTO) {
        UUID userId = UUID.fromString(keycloakUserDTO.getUserId());

        boolean userExists = userRepository.existsById(userId);
        if (userExists) {
            throw new UserAlreadyExistException("User already exists with ID: " + keycloakUserDTO.getUserId());
        }

        User newUser = userMapper.mapKeyCloakDtoToUser(keycloakUserDTO);
        userRepository.save(newUser);

    }

    @Override
    public UserDTO updateUser(UUID userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with Id: " + userId));
        user = userMapper.updateUserInitialAccount(user, userDTO);

        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            roleEventPublisher.publishRoleAssignments(userId.toString(), userDTO.getRoles());
        }
        return userMapper.mapUserToUserDTO(userRepository.save(user));
    }

    @Override
    public void addOwnedProduct(String userId, String productId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with Id: " + userId));

        user.getOwnedProductIds().add(UUID.fromString(productId));
        userRepository.save(user);
    }

    @Override
    public void markProfileAsCompleted(String id) {

    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }
}
