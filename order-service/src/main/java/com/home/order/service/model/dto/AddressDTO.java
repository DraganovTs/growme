package com.home.order.service.model.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 30, message = "First name cannot exceed 30 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 30, message = "Last name cannot exceed 30 characters")
    private String lastName;

    @NotBlank(message = "Street address cannot be blank")
    @Size(max = 30, message = "Street address cannot exceed 30 characters")
    private String street;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 30, message = "City cannot exceed 30 characters")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Size(min = 2, max = 20, message = "State must be 20 characters")
    private String state;

    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;
}
