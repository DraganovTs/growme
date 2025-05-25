package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SyncUserResponseDTO {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @NotNull(message = "Username cannot be null")
    private String username;

    @NotNull(message = "Email cannot be null")
    private String email;

    @NotNull(message = "Account status cannot be null")
    private AccountStatus accountStatus;

    public SyncUserResponseDTO(KeycloakUserDTO keycloakUserDTO) {
        this.userId = keycloakUserDTO.getUserId();
        this.username = keycloakUserDTO.getUsername();
        this.email = keycloakUserDTO.getEmail();
        this.accountStatus = keycloakUserDTO.getAccountStatus();
    }
}
