package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(
        name = "User",
        description = "Complete user profile information"
)
public class UserDTO {
    @Schema(
            description = "Unique username for login",
            example = "johndoe",
            minLength = 3,
            maxLength = 20,
            pattern = "^[a-zA-Z0-9_]+$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3-20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can only contain letters, numbers and underscores")
    private String username;

    @Schema(
            description = "Verified email address",
            example = "john.doe@example.com",
            format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(
            description = "Legal first name",
            example = "John",
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "First name is required")
    @Size(max = 30, message = "First name cannot exceed 30 characters")
    private String firstName;

    @Schema(
            description = "Legal last name",
            example = "Doe",
            maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Last name is required")
    @Size(max = 30, message = "Last name cannot exceed 30 characters")
    private String lastName;

    @Schema(
            description = "Contact phone number in E.164 format",
            example = "+1234567890",
            pattern = "^\\+?[0-9]{10,15}$"
    )
    @Pattern(regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be 10-15 digits, optionally starting with +")
    private String phone;

    @Schema(
            description = "Physical mailing address",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Valid
    @NotNull(message = "Address is required")
    private AddressDTO address;

    @Schema(
            description = "Assigned user roles",
            example = "[\"Seller\", \"Buyer\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "At least one role is required")
    private List<@NotBlank String> roles;

    @Schema(
            description = "Current account status",
            implementation = AccountStatus.class,
            defaultValue = "ACTIVE"
    )
    @NotNull(message = "Account status is required")
    private AccountStatus accountStatus;
}