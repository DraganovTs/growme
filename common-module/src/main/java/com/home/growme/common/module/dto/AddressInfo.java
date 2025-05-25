package com.home.growme.common.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfo {
    @NotBlank(message = "Street address cannot be blank")
    @Size(max = 30, message = "Street address cannot exceed 30 characters")
    private String street;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 30, message = "City cannot exceed 30 characters")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Size(min = 20, max = 20, message = "State must be 20 characters")
    private String state;

    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;
}
