package com.home.keycloak.api.model.dto;

public record UserRegistrationRecord(String username, String password, String firstName, String lastName, String email) {
}
