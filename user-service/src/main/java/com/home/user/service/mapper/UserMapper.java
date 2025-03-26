package com.home.user.service.mapper;


import com.home.user.service.exception.NotValidUserRoleException;
import com.home.user.service.model.dto.AddressDto;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.Address;
import com.home.user.service.model.entity.User;
import com.home.user.service.model.enums.AccountStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserMapper {

    public User mapKeyCloakDtoToUser(KeycloakUserDTO keycloakUserDTO) {
        return User.builder()
                .userId(UUID.fromString(keycloakUserDTO.getUserId()))
                .username(keycloakUserDTO.getUsername())
                .email(keycloakUserDTO.getEmail())
                .roles(new ArrayList<>(List.of("USER_PENDING")))
                .accountStatus(AccountStatus.PENDING)
                .address(null)
                .createdAt(new Date())
                .updatedAt(new Date())
                .ownedProductIds(new ArrayList<>())
                .purchasedOrderIds(new ArrayList<>())
                .build();
    }

    public KeycloakUserDTO mapUserToKeycloakDto(User user) {
        return KeycloakUserDTO.builder()
                .userId(user.getUserId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .accountStatus(user.getAccountStatus())
                .build();
    }


    private String extractValidRole(List<String> keycloakRoles) {

        final Set<String> ALLOWED_ROLES = Set.of("BUYER", "SELLER", "ADMIN");


        return keycloakRoles.stream()
                .filter(ALLOWED_ROLES::contains)
                .findFirst()
                .orElseThrow(() -> new NotValidUserRoleException("No valid role found"));
    }

    public User updateUserInitialAccount(User user, UserDTO userDTO) {
        return user.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phone(userDTO.getPhone())
                .address(mapAddressDTOToAddress(userDTO.getAddress()))
                .roles(new ArrayList<>(userDTO.getRoles()))
                .accountStatus(AccountStatus.ACTIVE)
                .updatedAt(new Date())
                .ownedProductIds(new ArrayList<>())
                .purchasedOrderIds(new ArrayList<>())
                .build();
    }

    private Address mapAddressDTOToAddress(AddressDto addressDto) {
        return Address.builder()
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .zipCode(addressDto.getZipCode())
                .build();
    }

    public UserDTO mapUserToUserDTO(User save) {
        return null;
    }
}
