package com.home.user.service.mapper;


import com.home.growme.common.module.dto.AddressInfo;
import com.home.growme.common.module.dto.UserInfo;
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
                .roles(new ArrayList<>())
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

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(mapAddressDTOToAddress(userDTO.getAddress()));
        user.setRoles(new ArrayList<>(userDTO.getRoles()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setUpdatedAt(new Date());
        user.setOwnedProductIds(new ArrayList<>());
        user.setPurchasedOrderIds(new ArrayList<>());

        return user;
    }



    public UserDTO mapUserToUserDTO(User save) {
        return UserDTO.builder()
                .username(save.getUsername())
                .email(save.getEmail())
                .firstName(save.getFirstName())
                .lastName(save.getLastName())
                .phone(save.getPhone())
                .address(mapAddressToAddressDTO(save.getAddress()))
                .roles(save.getRoles())
                .accountStatus(save.getAccountStatus())
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

    private AddressDto mapAddressToAddressDTO(Address address) {
        return AddressDto.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .build();
    }

    public UserInfo mapUserToUserInfo(User user) {
        AddressInfo addressInfo = AddressInfo.builder()
                .street(user.getAddress().getStreet())
                .city(user.getAddress().getCity())
                .state(user.getAddress().getState())
                .zipCode(user.getAddress().getZipCode())
                .build();

        return UserInfo.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .addressInfo(addressInfo)
                .build();
    }
}
