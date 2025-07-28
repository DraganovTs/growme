package com.home.keycloak.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import com.home.keycloak.api.service.KeycloakUserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(KeycloakUserController.class)
@ActiveProfiles("test")
@Import({KeycloakUserControllerTests.TestSecurityConfig.class, KeycloakUserControllerTests.DisableSecurityConfig.class})
public class KeycloakUserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KeycloakUserService keycloakUserService;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Primary
        public JwtDecoder jwtDecoder() {
            return token -> Jwt.withTokenValue("mock-token")
                    .header("alg", "none")
                    .claim("sub", "mock-user")
                    .build();
        }
    }

    @TestConfiguration
    @Profile("test")
    static class DisableSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth->auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void testCreateUser() throws Exception {
        UserRegistrationRecord request = new UserRegistrationRecord(
                "testuser", "password", "Test", "User", "test@example.com");

        Mockito.when(keycloakUserService.createUser(any()))
                .thenReturn(request);

        mockMvc.perform(post("/api/usersk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(request)));
    }
}