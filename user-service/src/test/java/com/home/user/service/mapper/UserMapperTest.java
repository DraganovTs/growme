package com.home.user.service.mapper;

import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.model.dto.AddressDTO;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.entity.Address;
import com.home.user.service.model.entity.User;
import com.home.user.service.model.enums.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {
    private UserMapper userMapper;

    @BeforeEach
    void setup() {
        userMapper = new UserMapper();
    }

    @Test
    void mapKeyCloakDtoToUser_shouldMapFieldsCorrectly() {
        String uuid = UUID.randomUUID().toString();
        KeycloakUserDTO dto = KeycloakUserDTO.builder()
                .userId(uuid)
                .username("testUser")
                .email("test@example.com")
                .build();

        User user = userMapper.mapKeyCloakDtoToUser(dto);

        assertEquals(UUID.fromString(uuid), user.getUserId());
        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertNull(user.getRoles());
        assertEquals(AccountStatus.PENDING, user.getAccountStatus());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.getOwnedProductIds().isEmpty());
        assertTrue(user.getPurchasedOrderIds().isEmpty());
    }

    @Test
    void mapUserToKeycloakDto_shouldMapFieldsCorrectly() {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .username("user123")
                .email("user123@example.com")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        KeycloakUserDTO dto = userMapper.mapUserToKeycloakDto(user);

        assertEquals(user.getUserId().toString(), dto.getUserId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getAccountStatus(), dto.getAccountStatus());
    }



    @Test
    void updateUserInitialAccount_shouldUpdateUserFields() {
        User user = User.builder().build();
        UserDTO dto = UserDTO.builder()
                .username("newUser")
                .email("new@example.com")
                .firstName("First")
                .lastName("Last")
                .phone("1234567890")
                .address(AddressDTO.builder()
                        .street("Street 1")
                        .city("City")
                        .state("State")
                        .zipCode("00000")
                        .build())
                .roles(List.of("BUYER"))
                .build();

        User updatedUser = userMapper.updateUserInitialAccount(user, dto);

        assertEquals("newUser", updatedUser.getUsername());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals("First", updatedUser.getFirstName());
        assertEquals("Last", updatedUser.getLastName());
        assertEquals("1234567890", updatedUser.getPhone());
        assertNotNull(updatedUser.getAddress());
        assertEquals("Street 1", updatedUser.getAddress().getStreet());
        assertEquals(List.of("BUYER"), updatedUser.getRoles());
        assertEquals(AccountStatus.ACTIVE, updatedUser.getAccountStatus());
        assertNotNull(updatedUser.getUpdatedAt());
    }

    @Test
    void mapUserToUserDTO_shouldMapAllFields() {
        Address address = Address.builder()
                .street("Street 1")
                .city("City")
                .state("State")
                .zipCode("12345")
                .build();

        User user = User.builder()
                .username("user1")
                .email("user1@example.com")
                .firstName("First")
                .lastName("Last")
                .phone("000")
                .address(address)
                .roles(List.of("SELLER"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UserDTO userDTO = userMapper.mapUserToUserDTO(user);

        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getPhone(), userDTO.getPhone());
        assertNotNull(userDTO.getAddress());
        assertEquals(address.getStreet(), userDTO.getAddress().getStreet());
        assertEquals(user.getRoles(), userDTO.getRoles());
        assertEquals(user.getAccountStatus(), userDTO.getAccountStatus());
    }

    @Test
    void mapUserToUserInfo_shouldMapAllFields() {
        Address address = Address.builder()
                .street("Main St")
                .city("Springfield")
                .state("IL")
                .zipCode("62704")
                .build();

        User user = User.builder()
                .username("userInfo")
                .email("info@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("555-1234")
                .address(address)
                .build();

        UserInfo userInfo = userMapper.mapUserToUserInfo(user);

        assertEquals(user.getUsername(), userInfo.getUsername());
        assertEquals(user.getEmail(), userInfo.getEmail());
        assertEquals(user.getFirstName(), userInfo.getFirstName());
        assertEquals(user.getLastName(), userInfo.getLastName());
        assertEquals(user.getPhone(), userInfo.getPhone());
        assertNotNull(userInfo.getAddressInfo());
        assertEquals(address.getStreet(), userInfo.getAddressInfo().getStreet());
    }

    @Test
    void updateUserFromDto_shouldUpdateUserFields() {
        User user = User.builder()
                .username("oldUser")
                .email("old@example.com")
                .build();

        KeycloakUserDTO dto = KeycloakUserDTO.builder()
                .username("newUser")
                .email("new@example.com")
                .build();

        userMapper.updateUserFromDto(dto, user);

        assertEquals("newUser", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
    }
}
