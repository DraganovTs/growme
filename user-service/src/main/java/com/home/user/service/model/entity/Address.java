package com.home.user.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Address {
    @Column(name = "street")
    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street name cannot exceed 100 characters")
    private String street;

    @Column(name = "city")
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City name cannot exceed 50 characters")
    private String city;

    @Column(name = "state")
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State name cannot exceed 50 characters")
    private String state;

    @Column(name = "zip_code")
    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "Invalid zip code format")
    private String zipCode;
}
