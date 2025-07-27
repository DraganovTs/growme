package com.home.keycloak.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import com.home.keycloak.api.service.KeycloakUserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(KeycloakUserController.class)
@ActiveProfiles("test")
public class KeycloakUserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KeycloakUserService keycloakUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create a user and return 200 with response body")
    void testCreateUser() throws Exception {
        // Given
        UserRegistrationRecord request = new UserRegistrationRecord(
                "testuser", "password", "Test", "User", "test@example.com");

        Mockito.when(keycloakUserService.createUser(any(UserRegistrationRecord.class)))
                .thenReturn(request); // echo the same object back for simplicity

        // When & Then
        mockMvc.perform(post("/api/usersk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(request)));
    }
}
