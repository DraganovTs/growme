package com.home.user.service.service.impl;

import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.mapper.UserMapper;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.User;
import com.home.user.service.publisher.RoleEventPublisher;
import com.home.user.service.repository.UserRepository;
import com.home.user.service.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleEventPublisher roleEventPublisher;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           RoleEventPublisher roleEventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleEventPublisher = roleEventPublisher;
    }

    @Override
    public KeycloakUserDTO syncUserFromKeycloak(KeycloakUserDTO keycloakUserDTO) {
        User existingUser = userRepository.findById(UUID.fromString(keycloakUserDTO.getUserId()))
                .orElse(null);

        if (existingUser == null) {
            User newUser = userMapper.mapKeyCloakDtoToUser(keycloakUserDTO);
            return userMapper.mapUserToKeycloakDto(userRepository.save(newUser));

        }
        return userMapper.mapUserToKeycloakDto(existingUser);
    }

    @Override
    public UserDTO getUserById(UUID id) {
        return null;
    }



    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user = userMapper.updateUserInitialAccount(user,userDTO);

        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            roleEventPublisher.publishRoleAssignments(id.toString(), userDTO.getRoles());
        }
        return userMapper.mapUserToUserDTO(userRepository.save(user));
    }

    @Override
    public void markProfileAsComplete(UUID id) {

    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public List<UserDTO> getUsersByRole(String role) {
        return List.of();
    }
}
