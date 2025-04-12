package com.home.user.service.service.impl;

import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.mapper.UserMapper;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.UserQueryService;
import com.home.user.service.util.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator validator;


    public UserQueryServiceImpl(UserRepository userRepository, UserMapper userMapper, UserValidator validator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.validator = validator;
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    log.debug("Found user with ID: {}", userId);
                    return userMapper.mapUserToUserDTO(user);
                })
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });
    }

    @Override
    public User getUserEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User entity not found: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });
    }

    @Override
    public String getUserEmail(UUID userId) {
        return userRepository.findEmailById(userId)
                .orElseThrow(() -> {
                    log.warn("User email not found for ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });
    }

    @Override
    public List<UserDTO> getUsersByRole(String role) {
        validator.validateRole(role);
        return userRepository.findByRolesContaining(role).stream()
                .map(userMapper::mapUserToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> getUsersOwnedProducts(String userId) {
        validator.validateUserId(userId);
        return userRepository.findById(UUID.fromString(userId))
                .map(user -> Collections.unmodifiableList(user.getOwnedProductIds()))
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


}
