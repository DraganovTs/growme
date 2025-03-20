package com.home.user.service.mapper;


import com.home.user.service.exception.NotValidUserRoleException;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class UserMapper {

    public User mapKeyCloakDtoToUser(KeycloakUserDTO keycloakUserDTO) {
        return User.builder()
                .userId(UUID.fromString(keycloakUserDTO.getUserId()))
                .username(keycloakUserDTO.getUsername())
                .email(keycloakUserDTO.getEmail())
                .role(extractValidRole(keycloakUserDTO.getRoles()))
                .build();
    }

    public KeycloakUserDTO mapUserToKeycloakDto(User user) {
        return KeycloakUserDTO.builder()
                .userId(user.getUserId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRole().lines().toList())
                .build();
    }


    private String extractValidRole(List<String> keycloakRoles){

        final Set<String> ALLOWED_ROLES = Set.of("BUYER", "SELLER", "ADMIN");


        return  keycloakRoles.stream()
                .filter(ALLOWED_ROLES::contains)
                .findFirst()
                .orElseThrow(()-> new NotValidUserRoleException("No valid role found"));
    }
}
