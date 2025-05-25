package com.home.user.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Column(name = "street")
    @Size(max = 100, message = "Street name cannot exceed 100 characters")
    private String street;

    @Column(name = "city")
    @Size(max = 50, message = "City name cannot exceed 50 characters")
    private String city;

    @Column(name = "state")
    @Size(max = 50, message = "State name cannot exceed 50 characters")
    private String state;

    @Column(name = "zip_code")
    @Pattern(regexp = "^[0-9]{4}", message = "Invalid zip code format")
    private String zipCode;
}
