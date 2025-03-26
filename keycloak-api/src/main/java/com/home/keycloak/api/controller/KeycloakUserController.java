package com.home.keycloak.api.controller;

import com.home.keycloak.api.model.dto.UserRegistrationRecord;
import com.home.keycloak.api.service.KeycloakRoleUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usersk")
public class KeycloakUserController {

    private final KeycloakRoleUserService keycloakRoleUserService;

    public KeycloakUserController(KeycloakRoleUserService keycloakRoleUserService) {
        this.keycloakRoleUserService = keycloakRoleUserService;
    }


    @PostMapping
    public UserRegistrationRecord createUser(@RequestBody UserRegistrationRecord userRegistrationRecord) {
        return keycloakRoleUserService.createUser(userRegistrationRecord);
    }
}
