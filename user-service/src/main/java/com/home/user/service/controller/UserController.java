package com.home.user.service.controller;

import com.home.growme.common.module.dto.UserInfo;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing user accounts in the GrowMe platform.
 * Provides endpoints for user synchronization, updates, deletion, and information retrieval.
 */
@Tag(
        name = "User Management API",
        description = """
                Complete set of REST APIs for user account management in GrowMe platform.
                Includes operations for:
                - User synchronization with identity provider
                - Profile updates
                - Account deletion
                - Information retrieval
                
                """
)
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Synchronize user from Keycloak",
            description = """
                        Triggers synchronization of a Keycloak-registered user with the application’s database.
                        Should be called after user registration in Keycloak.
                    """,
            operationId = "syncUserFromKeycloak"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Sync accepted - processing asynchronously"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/sync")
    public ResponseEntity<Void> syncUserFromKeycloak(@Valid @RequestBody KeycloakUserDTO request) {
        log.debug("Synchronizing user data for ID: {}", request.getUserId());
        if (!userService.existsById(request.getUserId())) {
            log.debug("User not found. Creating new user for ID: {}", request.getUserId());
            userService.requestAccountCreation(request);
        } else {
            userService.requestSyncUserData(request);
        }
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Update user profile",
            description = """
                        Updates user details such as address, contact, and role information.
                        Only the user or an admin should perform this operation.
                    """,
            operationId = "updateUser"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Email or username conflict", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable UUID userId, @Valid @RequestBody UserDTO updateRequest) {
        userService.requestAccountUpdate(userId, updateRequest);
    }


    @Operation(
            summary = "Delete a user (soft delete)",
            description = "Performs a logical (soft) delete of the user account by UUID.",
            operationId = "deleteUser"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.requestAccountDeletion(userId);
    }


    @Operation(
            summary = "Retrieve user info for internal use",
            description = "Fetches user name, email, and roles. Used internally for emails or admin dashboards.",
            operationId = "getUserInfo",
            hidden = true
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User info retrieved", content = @Content(schema = @Schema(implementation = UserInfo.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/userinfo/{userId}")
    public ResponseEntity<UserInfo> getUserName(@PathVariable String userId) {
        UserInfo userInfo = userService.getUserInformation(userId);
        return ResponseEntity.ok(userInfo);
    }

    @Operation(
            summary = "Check if user profile is complete",
            description = "Returns true if user has submitted all required profile information.",
            operationId = "checkUserProfile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile check result", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/profile-complete/{userId}")
    public ResponseEntity<Boolean> checkUserProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.requestCheckUserProfile(userId));
    }

}
