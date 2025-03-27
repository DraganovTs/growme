package com.home.keycloak.api.controller;

import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import com.home.keycloak.api.service.KeycloakUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usersk")
public class KeycloakUserController {

    private final KeycloakUserService keycloakUserService;

    public KeycloakUserController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }


    @PostMapping
    public UserRegistrationRecord createUser(@RequestBody UserRegistrationRecord userRegistrationRecord) {
        return keycloakUserService.createUser(userRegistrationRecord);
    }


}
