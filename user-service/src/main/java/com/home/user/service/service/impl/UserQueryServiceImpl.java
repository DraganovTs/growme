package com.home.user.service.service.impl;

import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.mapper.UserMapper;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserQueryServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::mapUserToUserDTO)
                .orElseThrow(() -> new UserNotFoundException("User not found whit ID: " + userId));
    }

    @Override
    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findByRolesContaining(role).stream()
                .map(userMapper::mapUserToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> getUsersOwnedProducts(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .map(user -> Collections.unmodifiableList(user.getOwnedProductIds()))
                .orElseThrow(() -> new UserNotFoundException("User not found whit ID: " + userId));
    }
}
