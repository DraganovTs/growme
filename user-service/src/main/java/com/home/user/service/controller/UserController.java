package com.home.user.service.controller;

import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sync")
    public ResponseEntity<KeycloakUserDTO> syncUserFromKeycloak(@RequestBody KeycloakUserDTO KeycloakUserDTO) {
        KeycloakUserDTO user = userService.syncUserFromKeycloak(KeycloakUserDTO);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userUpdate) {
        UserDTO updatedUser = userService.updateUser(id, userUpdate);
        return ResponseEntity.ok(updatedUser);
    }
}
