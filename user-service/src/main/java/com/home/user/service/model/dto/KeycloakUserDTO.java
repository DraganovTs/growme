package com.home.user.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KeycloakUserDTO {
    @NotBlank(message = "User ID  is not created")
    private String userId;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Role is required")
    private List<String> roles;
}
