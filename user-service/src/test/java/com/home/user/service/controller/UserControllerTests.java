package com.home.user.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.exception.UserAlreadyExistException;
import com.home.user.service.exception.UserNotFoundException;
import com.home.user.service.model.dto.AddressDTO;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.model.enums.AccountStatus;
import com.home.user.service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSyncUserFromKeycloak_UserCreatedSuccessfully() throws Exception {
        KeycloakUserDTO userDTO = KeycloakUserDTO.builder()
                .userId("550e8400-e29b-41d4-a716-446655440000")
                .username("john_doe")
                .email("john.doe@example.com")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(userService.existsById(userDTO.getUserId())).thenReturn(false);

        mockMvc.perform(post("/api/users/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isAccepted());

        verify(userService).requestAccountCreation(any(KeycloakUserDTO.class));
    }

    @Test
    public void testSyncUserFromKeycloak_UserAlreadyExists() throws Exception {
        KeycloakUserDTO userDTO = KeycloakUserDTO.builder()
                .userId("550e8400-e29b-41d4-a716-446655440000")
                .username("john_doe")
                .email("john.doe@example.com")
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(userService.existsById(userDTO.getUserId())).thenReturn(true);

        mockMvc.perform(post("/api/users/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isAccepted());

        verify(userService).requestSyncUserData(any(KeycloakUserDTO.class));
    }

    @Test
    public void testSyncUserFromKeycloak_InvalidRequest() throws Exception {
        KeycloakUserDTO invalidUserDTO = KeycloakUserDTO.builder()
                .userId("invalid-id")
                .username("")
                .email("invalid-email")
                .accountStatus(null)
                .build();

        mockMvc.perform(post("/api/users/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser_SuccessfulUpdate() throws Exception {
        UserDTO validUserDTO = UserDTO.builder()
                .username("johndoe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .address(AddressDTO.builder().city("City").state("State").zipCode("12345").street("Address Line 1").build())
                .roles(List.of("Buyer"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/api/users/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDTO)))
                .andExpect(status().isNoContent());

        verify(userService).requestAccountUpdate(userId, validUserDTO);
    }

    @Test
    public void testUpdateUser_InvalidRequest() throws Exception {
        UserDTO invalidUserDTO = UserDTO.builder()
                .username("")
                .email("invalid-email")
                .firstName("")
                .lastName("Doe")
                .build();

        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/api/users/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser_UserNotFound() throws Exception {
        UserDTO validUserDTO = UserDTO.builder()
                .username("johndoe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .address(AddressDTO.builder().city("City").state("State").zipCode("12345").street("Address Line 1").build())
                .roles(List.of("Buyer"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UUID userId = UUID.randomUUID();

        doThrow(new UserNotFoundException("User not found"))
                .when(userService).requestAccountUpdate(userId, validUserDTO);

        mockMvc.perform(put("/api/users/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateUser_Conflict() throws Exception {
        UserDTO conflictingUserDTO = UserDTO.builder()
                .username("existingUser")
                .email("existing.email@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .phone("+1987654321")
                .address(AddressDTO.builder().city("City").state("State").zipCode("54321").street("Address Line 2").build())
                .roles(List.of("Seller"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UUID userId = UUID.randomUUID();

        doThrow(new UserAlreadyExistException("Conflict with username or email"))
                .when(userService).requestAccountUpdate(userId, conflictingUserDTO);

        mockMvc.perform(put("/api/users/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictingUserDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testDeleteUser_SuccessfulDeletion() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        verify(userService).requestAccountDeletion(userId);
    }

    @Test
    public void testDeleteUser_UserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        doThrow(new UserNotFoundException("User not found"))
                .when(userService).requestAccountDeletion(userId);

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserName_Success() throws Exception {
        String userId = "550e8400-e29b-41d4-a716-446655440000";
        UserInfo userInfo = UserInfo.builder()
                .username("john_doe")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .build();

        when(userService.getUserInformation(userId)).thenReturn(userInfo);

        mockMvc.perform(get("/api/users/userinfo/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    public void testGetUserName_UserNotFound() throws Exception {
        String userId = "550e8400-e29b-41d4-a716-446655440000";

        doThrow(new UserNotFoundException("User not found"))
                .when(userService).getUserInformation(userId);

        mockMvc.perform(get("/api/users/userinfo/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCheckUserProfile_CompleteProfile() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.requestCheckUserProfile(userId)).thenReturn(true);

        mockMvc.perform(get("/api/users/profile-complete/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testCheckUserProfile_IncompleteProfile() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.requestCheckUserProfile(userId)).thenReturn(false);

        mockMvc.perform(get("/api/users/profile-complete/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void testCheckUserProfile_UserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        doThrow(new UserNotFoundException("User not found"))
                .when(userService).requestCheckUserProfile(userId);

        mockMvc.perform(get("/api/users/profile-complete/" + userId))
                .andExpect(status().isNotFound());
    }
}