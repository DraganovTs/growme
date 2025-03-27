package com.home.keycloak.api.service;

public interface KeycloakRoleService {

    void assignRole(String userId, String roleName);

    void reassignRole(String userId, String roleName);
}
