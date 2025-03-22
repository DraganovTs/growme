package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
import lombok.Data;

@Data
public class SyncUserResponseDTO {
    private String userId;
    private String username;
    private String email;
    private AccountStatus accountStatus;

    public SyncUserResponseDTO(KeycloakUserDTO keycloakUserDTO) {
        this.userId = keycloakUserDTO.getUserId();
        this.username = keycloakUserDTO.getUsername();
        this.email = keycloakUserDTO.getEmail();
        this.accountStatus = keycloakUserDTO.getAccountStatus();
    }
}
