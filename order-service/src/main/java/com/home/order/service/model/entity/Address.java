package com.home.order.service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Address {

    @Column(name = "first_name",nullable = false)
    private String firstName;
    @Column(name = "last_name",nullable = false)
    private String lastName;
    @Column(name = "street",nullable = false)
    private String street;
    @Column(name = "city",nullable = false)
    private String city;
    @Column(name = "state",nullable = false)
    private String state;
    @Column(name = "zip_code",nullable = false)
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
