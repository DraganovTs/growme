package com.home.keycloak.api.controller;

import com.home.keycloak.api.service.KeycloakRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class KeycloakRoleApi {

    private final KeycloakRoleService roleService;

    public KeycloakRoleApi(KeycloakRoleService roleService) {
        this.roleService = roleService;
    }

    @PutMapping("/assign-role/user/{userId}")
    public ResponseEntity<?> assignRole(@PathVariable String userId, @RequestParam String roleName) {
        roleService.assignRole(userId, roleName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
