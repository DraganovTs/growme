package com.home.user.service.model.dto;

import com.home.user.service.model.enums.AccountStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20)
    private String username;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;

    @Valid
    private AddressDto address;

    @NotBlank(message = "Role is required")
    private String role;

    private AccountStatus accountStatus;
}
