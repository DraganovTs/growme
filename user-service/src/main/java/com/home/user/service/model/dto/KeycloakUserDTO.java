package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class KeycloakUserDTO {
    @NotBlank(message = "User ID  is not created")
    private String userId;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    private String email;
    private AccountStatus accountStatus;
}
