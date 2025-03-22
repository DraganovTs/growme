package com.home.user.service.controller;

import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.SyncUserResponseDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sync")
    public ResponseEntity<SyncUserResponseDTO> syncUserFromKeycloak(@RequestBody KeycloakUserDTO keycloakUserDTO) {
        logger.info("Syncing user from Keycloak: {}", keycloakUserDTO);
        KeycloakUserDTO syncedUser = userService.syncUserFromKeycloak(keycloakUserDTO);
        return ResponseEntity.ok(new SyncUserResponseDTO(syncedUser));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userUpdate) {
        UserDTO updatedUser = userService.updateUser(id, userUpdate);
        return ResponseEntity.ok(updatedUser);
    }


}
