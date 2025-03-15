package com.home.user.service.mapper;


import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

    public User mapKeyCloakDtoToUser(KeycloakUserDTO keycloakUserDTO) {
        return User.builder()
                .userId(UUID.fromString(keycloakUserDTO.getUserId()))
                .username(keycloakUserDTO.getUsername())
                .email(keycloakUserDTO.getEmail())
                .role(keycloakUserDTO.getRole())
                .build();
    }

    public KeycloakUserDTO mapUserToKeycloakDto(User user) {
        return KeycloakUserDTO.builder()
                .userId(user.getUserId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
