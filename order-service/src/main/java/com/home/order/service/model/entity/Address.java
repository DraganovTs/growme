package com.home.order.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Address {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    @Column(name = "first_name",nullable = false)
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @NotBlank(message = "Street is required")
    @Size(min = 5, max = 30, message = "Street must be between 5 and 30 characters")
    @Column(name = "street", nullable = false)
    private String street;
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 30, message = "City must be between 2 and 30 characters")
    @Column(name = "city", nullable = false)
    private String city;
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 30, message = "State must be between 2 and 30 characters")
    @Column(name = "state", nullable = false)
    private String state;
    @NotBlank(message = "Zip code is required")
    @Column(name = "zip_code", nullable = false)
    private String zipCode;



    public Address(String firstName, String lastName, String street, String city, String state, String zipCode) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.street = Objects.requireNonNull(street);
        this.city = Objects.requireNonNull(city);
        this.state = Objects.requireNonNull(state);
        this.zipCode = Objects.requireNonNull(zipCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(firstName, address.firstName) && Objects.equals(lastName, address.lastName)
                && Objects.equals(street, address.street) && Objects.equals(city, address.city)
                && Objects.equals(state, address.state) && Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, street, city, state, zipCode);
    }
}
