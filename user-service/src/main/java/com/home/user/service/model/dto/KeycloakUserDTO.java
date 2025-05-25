package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;



@Schema(
        name = "Keycloak User",
        description = """
        Represents a user account provisioned in Keycloak identity provider
        that needs to be synchronized with the application database.
        Contains the minimal user information required for initial synchronization.
        """
)
@Data
@Builder
public class KeycloakUserDTO {

    @Schema(
            description = "Unique identifier from Keycloak in UUID format",
            example = "550e8400-e29b-41d4-a716-446655440000",
            pattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "User ID is required")
    private String userId;

    @Schema(
            description = "Unique username created during Keycloak registration",
            example = "john_doe",
            minLength = 3,
            maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "^[a-zA-Z0-9_]+$"
    )
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3-20 characters")
    private String username;

    @Schema(
            description = "Verified email address from Keycloak",
            example = "john.doe@example.com",
            format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(
            description = "Current account status in the system",
            implementation = AccountStatus.class,
            example = "ACTIVE",
            defaultValue = "PENDING"
    )
    private AccountStatus accountStatus;
}
