package com.home.user.service.controller;

import com.home.growme.common.module.dto.UserInfo;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> syncUserFromKeycloak(@Valid @RequestBody KeycloakUserDTO request) {
        log.debug("Initiating user sync for Keycloak ID: {}", request.getUserId());
        userService.requestAccountCreation(request);
        return ResponseEntity.accepted().build();


    }

    @PutMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable UUID userId, @RequestBody UserDTO updateRequest) {
        userService.requestAccountUpdate(userId, updateRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.requestAccountDeletion(userId);
    }


    @GetMapping("/userinfo/{userId}")
    public ResponseEntity<UserInfo> getUserName(@PathVariable String userId) {
        UserInfo userInfo = userService.getUserInformation(userId);
        return ResponseEntity.ok(userInfo);
    }

}
