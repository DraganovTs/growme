package com.home.user.service.model.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {

    @NotBlank(message = "Street is required")
    private String street;


    @NotBlank(message = "City is required")
    private String city;


    @NotBlank(message = "State is required")
    private String state;


    @NotBlank(message = "Zip code is required")
    private String zipCode;
}
