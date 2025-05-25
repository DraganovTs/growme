package com.home.user.service.controller;

import com.home.growme.common.module.dto.UserInfo;
import com.home.growme.common.module.exceptions.ErrorResponseDTO;
import com.home.user.service.model.dto.KeycloakUserDTO;
import com.home.user.service.model.dto.UserDTO;
import com.home.user.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
        
        Requires ADMIN or appropriate permissions for write operations.
        """
)
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Synchronize user account from Keycloak",
            description = """
            Initial synchronization a user account created in Keycloak with the application database.
            This is typically called after successful user registration in Keycloak.
            
            """,
            operationId = "syncUserFromKeycloak"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "User sync request accepted",
                    headers = @Header(
                            name = "Location",
                            description = "URL to track sync status",
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists in system",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )

            )
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
            summary = "Update user account information",
            description = """
            Updates additional user information including roles, address, and contact details.
            Can only be performed by the account owner.
            
            """,
            operationId = "updateUser"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid update data",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PutMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable UUID userId,@Valid @RequestBody UserDTO updateRequest) {
        userService.requestAccountUpdate(userId, updateRequest);
    }



    @Operation(
            summary = "Delete user account",
            description = """
            Soft-deletes a user account from the system.
            Performs logical deletion rather than physical data removal.
         
            """,
            operationId = "deleteUser"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.requestAccountDeletion(userId);
    }



    @Operation(
            summary = "Get user information",
            description = """
            Retrieves basic user information for internal system use.
            Primarily used for email notifications and other internal processes.
       
            """,
            operationId = "getUserInfo",
            hidden = true
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User information retrieved",
                    content = @Content(
                            schema = @Schema(implementation = UserInfo.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @GetMapping("/userinfo/{userId}")
    public ResponseEntity<UserInfo> getUserName(@PathVariable String userId) {
        UserInfo userInfo = userService.getUserInformation(userId);
        return ResponseEntity.ok(userInfo);
    }

}
